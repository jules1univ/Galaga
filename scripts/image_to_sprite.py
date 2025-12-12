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



def main(args: list[str]) -> int:    
    if len(args) != 3:
        print("Usage: image_to_sprite <input_path> <output_path> <width>x<height>")
        return 1

    input_path = args[0]    
    if not os.path.exists(input_path):
        print(f"Input path '{input_path}' does not exist.")
        return 1

    if os.path.isdir(input_path):
        print(f"Input path '{input_path}' is a directory. Please provide a file path.")
        return 1
    
    output_path = args[1]
    if os.path.exists(output_path):
        print(f"Output path '{output_path}' already exists. Please provide a different file path.")
        return 1
    
    dimensions = args[2]
    if 'x' not in dimensions:
        print(f"Dimensions '{dimensions}' are not in the correct format. Use <width>x<height>.")
        return 1
    
    width, height = map(int, dimensions.split('x'))
    if width <= 0 or height <= 0:
        print(f"Dimensions '{dimensions}' must be positive integers.")
        return 1

    try:
        image = Image.open(input_path).convert("RGBA")
        image = image.resize((width, height))

        pixels = image.load()
        if pixels is None:
            print(f"Failed to load pixels from image '{input_path}'.")
            return 1
        
        with open(output_path, 'w') as f:
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
        print(f"Failed to open image '{input_path}': {e}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))