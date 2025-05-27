import os
from cairosvg import svg2png

def convert_svg_to_png_120x120_cairosvg(target_width=120, target_height=120):
    """
    Converts all SVG files in the current directory to 120x120 PNG files,
    saving the PNGs in the same directory.

    Args:
        target_width (int): Desired width of the output PNGs in pixels.
        target_height (int): Desired height of the output PNGs in pixels.
    """
    current_directory = os.getcwd() # Get the current working directory
    print(f"Running conversion in: {current_directory}")

    # Find all SVG files in the current directory, ignoring case
    svg_files = [f for f in os.listdir(current_directory) if f.lower().endswith('.svg')]

    if not svg_files:
        print(f"No SVG files found in '{current_directory}'.")
        return

    print(f"Found {len(svg_files)} SVG files. Converting to {target_width}x{target_height} PNGs...")

    for filename in svg_files:
        svg_path = os.path.join(current_directory, filename)
        png_filename = os.path.splitext(filename)[0] + '.png'
        png_path = os.path.join(current_directory, png_filename)

        try:
            with open(svg_path, 'rb') as f_in:
                svg_data = f_in.read()
                svg2png(
                    bytestring=svg_data,
                    write_to=png_path,
                    output_width=target_width,   # Set desired width
                    output_height=target_height  # Set desired height
                )
            print(f"Converted: '{filename}' -> '{png_filename}' ({target_width}x{target_height})")
        except Exception as e:
            print(f"Error converting '{filename}': {e}")

if __name__ == "__main__":
    # You can change these if you need a different size
    output_size = 120

    # For demonstration: create dummy SVG files if they don't exist
    if not os.path.exists("test_svg1.svg"):
        with open("test_svg1.svg", 'w') as f:
            f.write('<svg width="100" height="100"><circle cx="50" cy="50" r="40" fill="red" /></svg>')
        with open("test_svg2.svg", 'w') as f:
            f.write('<svg width="150" height="80"><rect x="10" y="10" width="130" height="60" fill="blue" /></svg>')
        print("Created dummy SVG files for demonstration in the current directory.")

    convert_svg_to_png_120x120_cairosvg(target_width=output_size, target_height=output_size)
    print("Conversion complete!")