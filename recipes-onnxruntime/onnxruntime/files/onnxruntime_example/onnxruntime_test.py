import numpy as np
import onnxruntime as rt
import cv2
import argparse
import time
import gc
import os

with open('labels_map.txt', 'r') as f:
    labels = [l.rstrip() for l in f]

def center_crop(img, out_height, out_width):
    height, width, _ = img.shape
    left   = int((width  - out_width)  / 2)
    right  = int((width  + out_width)  / 2)
    top    = int((height - out_height) / 2)
    bottom = int((height + out_height) / 2)
    return img[top:bottom, left:right]

def resize_with_aspectratio(img, out_height, out_width,
                             scale=87.5,
                             inter_pol=cv2.INTER_LINEAR):
    height, width, _ = img.shape
    new_height = int(100. * out_height / scale)
    new_width  = int(100. * out_width  / scale)
    if height > width:
        w = new_width
        h = int(new_height * height / width)
    else:
        h = new_height
        w = int(new_width * width / height)
    return cv2.resize(img, (w, h), interpolation=inter_pol)

def pre_process(img, dims):
    out_h, out_w, _ = dims
    img = resize_with_aspectratio(img, out_h, out_w)
    img = center_crop(img, out_h, out_w)
    img = np.asarray(img, dtype='float32')
    img -= [127.0, 127.0, 127.0]
    img /= [128.0, 128.0, 128.0]
    return img

def get_providers(args):
    ep = args.execution_provider

    if ep == 'WebGpuExecutionProvider':
        options = {
            'deviceId':           args.device_id,
            'dawnBackendType':    args.dawn_backend,
            'preferredLayout':    1,
            'enableGraphCapture': args.graph_capture,
        }
        print(f"  WebGPU options  : {options}")
        return [('WebGpuExecutionProvider', options)]

    elif ep == 'XnnpackExecutionProvider':
        options = {'intra_op_num_threads': str(args.num_threads)}
        print(f"  XNNPACK options : {options}")
        return [
            ('XnnpackExecutionProvider', options),
            'CPUExecutionProvider',
        ]

    else:  # CPU
        options = {'intra_op_num_threads': str(args.num_threads)}
        print(f"  CPU options     : {options}")
        return [('CPUExecutionProvider', options)]

def print_results(results, labels, active_ep,
                  warmup_ms, times_ms):
    print(f"\n{'='*55}")
    print(f"  EP             : {active_ep}")
    print(f"{'='*55}")

    result = reversed(results[0].argsort()[-5:])
    print("\nTop-5 Predictions:")
    for r in result:
        print(f"  {results[0][r]:.7f}  {labels[r]}")

    if not times_ms:
        print(f"\nSingle run: {warmup_ms:.3f}ms")
        return

    avg   = sum(times_ms) / len(times_ms)
    min_t = min(times_ms)
    max_t = max(times_ms)

    print(f"\nTiming ({len(times_ms)+1} total runs):")
    print(f"  {'Warmup (run 1)':<24}: {warmup_ms:>10.3f} ms"
          f"  ← includes shader compilation")
    print(f"  {'Average (runs 2+)':<24}: {avg:>10.3f} ms")
    print(f"  {'Min':<24}: {min_t:>10.3f} ms")
    print(f"  {'Max':<24}: {max_t:>10.3f} ms")

    print(f"\n  Individual runs (2+):")
    for i, t in enumerate(times_ms):
        marker = " ← outlier" if t == max_t and i == 0 else ""
        print(f"    Run {i+2:3d}: {t:>10.3f} ms{marker}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='ORT inference benchmark'
    )
    parser.add_argument("--image",  "-i",
        default='kitten.jfif')
    parser.add_argument("--labels", "-l",
        default='labels_map.txt')
    parser.add_argument("--model",  "-m",
        default='/root/onnxruntime_example/efficientnet-lite4.onnx')
    parser.add_argument("--execution_provider", "-e",
        default='CPUExecutionProvider',
        choices=[
            'WebGpuExecutionProvider',
            'CPUExecutionProvider',
            'XnnpackExecutionProvider',
        ])
    parser.add_argument("--runs", "-r",
        type=int, default=10,
        help="Benchmark runs after warmup (default: 10)")
    parser.add_argument("--dawn_backend", "-d",
        type=int, default=1,
        choices=[0, 1, 2, 3],
        help="0=default 1=Vulkan 2=D3D12 3=Metal")
    parser.add_argument("--device_id",
        type=int, default=0)
    parser.add_argument("--num_threads", "-t",
        type=int, default=2)
    parser.add_argument("--graph_capture", "-g",
        type=int, default=0,
        choices=[0, 1],
        help="Enable WebGPU graph capture (default: 0)")
    parser.add_argument("--verbose", "-v",
        action='store_true')
    args = parser.parse_args()

    # Logging
    rt.set_default_logger_severity(0 if args.verbose else 3)

    # Prepare input
    img = cv2.imread(args.image)
    if img is None:
        print(f"Error: cannot read '{args.image}'")
        os._exit(1)

    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = pre_process(img, (224, 224, 3))
    img_batch = np.expand_dims(img, axis=0)
    feeds = {"images:0": img_batch}

    # Load model
    print(f"\nLoading model...")
    print(f"  EP              : {args.execution_provider}")

    providers   = get_providers(args)
    sess_options = rt.SessionOptions()
    sess_options.graph_optimization_level = \
        rt.GraphOptimizationLevel.ORT_ENABLE_ALL

    sess = rt.InferenceSession(
        args.model,
        sess_options=sess_options,
        providers=providers
    )

    active_ep = sess.get_providers()[0]
    print(f"  Active EP       : {active_ep}")

    # Show input/output info
    for inp in sess.get_inputs():
        print(f"  Model input     : {inp.name} "
              f"shape={inp.shape} type={inp.type}")

    # Warmup
    print(f"\nWarmup run...")
    t0 = time.perf_counter()
    results = sess.run(["Softmax:0"], feeds)[0]
    warmup_ms = (time.perf_counter() - t0) * 1000
    print(f"  Warmup          : {warmup_ms:.3f}ms")

    # Benchmark
    times_ms = []
    if args.runs > 0:
        print(f"\nRunning {args.runs} benchmark iterations...")
        for i in range(args.runs):
            t0 = time.perf_counter()
            results = sess.run(["Softmax:0"], feeds)[0]
            t = (time.perf_counter() - t0) * 1000
            times_ms.append(t)
            print(f"  Run {i+2:3d}         : {t:>10.3f} ms")

    # Results
    print_results(results, labels, active_ep,
                  warmup_ms, times_ms)

    # Safe cleanup
    try:
        sess._sess = None
    except Exception:
        pass
    del sess
    gc.collect()
    gc.collect()
    time.sleep(0.5)
    os._exit(0)