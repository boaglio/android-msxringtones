"""Generates the Google Play feature graphic (1024x500) for MSX Ringtones. Needs Pillow.

Usage: python3 tools/generate_feature_graphic.py play/feature-graphic.png

Everything is drawn as pixel blocks: the beamed-notes icon from generate_icon.py and a small built-in 5x7
font, so the output is the same on any machine (no system fonts).
"""
import sys
from PIL import Image, ImageDraw

sys.path.insert(0, __file__.rsplit("/", 1)[0])

W, H = 1024, 500
BLUE = (0x29, 0x54, 0x9F)
DARK = (0x1B, 0x3A, 0x73)
WHITE = (255, 255, 255)
PALE = (0xC8, 0xD6, 0xF0)

# Same 16x16 grid as tools/generate_icon.py.
NOTE = [
    "................",
    ".....##########.",
    ".....##########.",
    ".....##......##.",
    ".....##......##.",
    ".....##......##.",
    ".....##......##.",
    ".....##......##.",
    ".....##......##.",
    ".....##......##.",
    ".....##......##.",
    "...####....####.",
    ".######..######.",
    ".######..######.",
    "..####....####..",
    "................",
]

# 5x7 pixel font, only the characters used below.
FONT = {
    "A": [".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"],
    "C": [".###.", "#...#", "#....", "#....", "#....", "#...#", ".###."],
    "D": ["####.", "#...#", "#...#", "#...#", "#...#", "#...#", "####."],
    "E": ["#####", "#....", "#....", "####.", "#....", "#....", "#####"],
    "F": ["#####", "#....", "#....", "####.", "#....", "#....", "#...."],
    "G": [".###.", "#...#", "#....", "#.###", "#...#", "#...#", ".###."],
    "I": [".###.", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."],
    "M": ["#...#", "##.##", "#.#.#", "#.#.#", "#...#", "#...#", "#...#"],
    "N": ["#...#", "##..#", "#.#.#", "#..##", "#...#", "#...#", "#...#"],
    "O": [".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "P": ["####.", "#...#", "#...#", "####.", "#....", "#....", "#...."],
    "R": ["####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"],
    "S": [".####", "#....", "#....", ".###.", "....#", "....#", "####."],
    "T": ["#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."],
    "U": ["#...#", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "X": ["#...#", "#...#", ".#.#.", "..#..", ".#.#.", "#...#", "#...#"],
    "Y": ["#...#", "#...#", ".#.#.", "..#..", "..#..", "..#..", "..#.."],
    ",": [".....", ".....", ".....", ".....", ".....", "..#..", ".#..."],
    "-": [".....", ".....", ".....", ".###.", ".....", ".....", "....."],
    " ": [".....", ".....", ".....", ".....", ".....", ".....", "....."],
}


def blocks(draw, grid, x0, y0, scale, color):
    for y, row in enumerate(grid):
        for x, c in enumerate(row):
            if c == "#":
                draw.rectangle([x0 + x * scale, y0 + y * scale, x0 + (x + 1) * scale - 1, y0 + (y + 1) * scale - 1], fill=color)


def text_width(text, scale):
    return (len(text) * 6 - 1) * scale  # 5 px per glyph + 1 px gap


def text(draw, s, x0, y0, scale, color, shadow=None):
    for i, ch in enumerate(s):
        x = x0 + i * 6 * scale
        if shadow:
            blocks(draw, FONT[ch], x + scale, y0 + scale, scale, shadow)
        blocks(draw, FONT[ch], x, y0, scale, color)


def main(out):
    img = Image.new("RGB", (W, H), BLUE)
    d = ImageDraw.Draw(img)

    # Faint MSX-style scanlines for texture.
    for y in range(0, H, 4):
        d.line([(0, y), (W, y)], fill=(0x26, 0x4F, 0x96))

    title, tagline = "MSX RINGTONES", "PSG, SCC AND FM-PAC FOR YOUR EARS"
    note_scale, title_scale, tag_scale = 14, 8, 3
    note_w = 16 * note_scale
    gap = 40
    block_w = note_w + gap + text_width(title, title_scale)
    x0 = (W - block_w) // 2
    note_y = (H - 16 * note_scale) // 2

    blocks(d, NOTE, x0 + note_scale, note_y + note_scale, note_scale, DARK)  # shadow
    blocks(d, NOTE, x0, note_y, note_scale, WHITE)

    tx = x0 + note_w + gap
    title_h = 7 * title_scale
    tag_h = 7 * tag_scale
    ty = (H - (title_h + 28 + tag_h)) // 2
    text(d, title, tx, ty, title_scale, WHITE, shadow=DARK)
    text(d, tagline, tx + (text_width(title, title_scale) - text_width(tagline, tag_scale)) // 2,
         ty + title_h + 28, tag_scale, PALE)

    img.save(out, optimize=True)
    print(f"wrote {out} ({W}x{H})")


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else "play/feature-graphic.png")
