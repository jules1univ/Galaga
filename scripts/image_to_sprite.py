import struct
import sys
import os
from PIL import Image

COLORS = {
    'W': (255, 255, 255, 255),
    'B': (0, 0, 255, 255),
    'R': (255, 0, 0, 255),
    'Y': (255, 255, 0, 255),
    'G': (0, 255, 0, 255),
    'C': (0, 255, 255, 255),
    'M': (255, 0, 255, 255),
    'O': (255, 165, 0, 255),
    'P': (255, 192, 203, 255),
    'L': (211, 211, 211, 255),
    'D': (169, 169, 169, 255),
    'N': (0, 0, 0, 0)
}

def get_nearest_color(r: int,g: int, b:int, a: int) -> str:
    nearest_color = 'N'
    min_distance = float('inf')
    for code, (cr, cg, cb, ca) in COLORS.items():
        distance = (r - cr) ** 2 + (g - cg) ** 2 + (b - cb) ** 2 + (a - ca) ** 2
        if distance < min_distance:
            min_distance = distance
            nearest_color = code
    return nearest_color



def process_file(file_in: str, file_out: str, scale: float = 1.0):

    try:
        image = Image.open(file_in).convert("RGBA")        
        width = int(image.width * scale)
        height = int(image.height * scale)
        

        image = image.resize((width, height))
        print("Final image size:", image.width, "x", image.height)

        pixels = image.load()
        if pixels is None:
            print(f"Failed to load pixels from image '{file_in}'.")
            return 1
        
        with open(file_out, 'w') as f:
            for y in range(height):
                row = []
                for x in range(width):
                    pixel = pixels[x, y]
                    
                    if isinstance(pixel, float):
                        packed = struct.pack("f", pixel)
                        r, g, b, a = struct.unpack("BBBB", packed)
                    elif isinstance(pixel, tuple):
                        r, g, b, a = pixel
                    else:
                        packed = struct.pack("I", pixel)
                        r, g, b, a = struct.unpack("BBBB", packed)    
                    row.append(get_nearest_color(r, g, b, a))
                f.write(''.join(row) + '\n')

    except Exception as e:
        print(f"Failed to process image '{file_in}': {e}")
        return False
    return True


def main(args: list[str]) -> int:    
    if len(args) < 2:
        print("Usage: image_to_sprite <input_path> <output_path> <scale>")
        return 1

    input_path = args[0]
    output_path = args[1]
    scale = float(args[2]) if len(args) >= 3 else 1.0

    files = []
    if os.path.isdir(input_path):
        for filename in os.listdir(input_path):
            if filename.lower().endswith(('.png', '.jpg', '.jpeg', '.bmp', '.gif')):
                files.append((os.path.join(input_path, filename), os.path.join(output_path, os.path.splitext(filename)[0] + '.spr')))
    else:
        files.append((input_path, output_path))

    for file_in, file_out in files:
        print(f"Processing '{file_in}' -> '{file_out}' with scale {scale}")
        success = process_file(file_in, file_out, scale)
        if not success:
            print(f"Failed to process '{file_in}'")
            return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))