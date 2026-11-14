"""Generates the MSX Ringtones launcher icon (adaptive vectors, legacy PNGs, Play icon). Needs Pillow."""
import sys, os
from PIL import Image, ImageDraw
# Usage: python3 tools/generate_icon.py app/src/main/res play
res = sys.argv[1]; out_preview = sys.argv[2]
BLUE = "#29549F"
# 16x16 pixel-art beamed eighth notes (♫). '#' = pixel.
GRID = [
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
px = [(x, y) for y, row in enumerate(GRID) for x, c in enumerate(row) if c == "#"]

# merge horizontal runs into rects for a compact vector path
def runs():
    for y, row in enumerate(GRID):
        x = 0
        while x < 16:
            if row[x] == "#":
                s = x
                while x < 16 and row[x] == "#": x += 1
                yield s, y, x - s
            else:
                x += 1

def path(scale, ox, oy):
    parts = []
    for x, y, w in runs():
        X = ox + x * scale; Y = oy + y * scale
        parts.append(f"M{X:g},{Y:g}h{w*scale:g}v{scale:g}h{-w*scale:g}z")
    return "".join(parts)

# Adaptive icon: 108dp canvas, safe zone 66dp. 16 px * 3.75 = 60dp, centred.
P = path(3.75, 24, 24)
vec = lambda color: f'''<?xml version="1.0" encoding="utf-8"?>
<!-- MSX Ringtones icon: pixel-art beamed notes. Generated; 16x16 grid in the 66dp safe zone. -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="{color}"
        android:pathData="{P}" />
</vector>
'''
open(f"{res}/drawable/ic_launcher_foreground.xml", "w").write(vec("#FFFFFFFF"))
open(f"{res}/drawable/ic_launcher_monochrome.xml", "w").write(vec("#FF000000"))

def render(size, rounded=True, bg=True):
    ss = 4; S = size * ss
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
    if bg:
        if rounded: d.rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * 0.18), fill=BLUE)
        else: d.rectangle([0, 0, S - 1, S - 1], fill=BLUE)
    cell = S * 0.72 / 16; o = (S - cell * 16) / 2
    for x, y in px:
        d.rectangle([o + x * cell, o + y * cell, o + (x + 1) * cell - 1, o + (y + 1) * cell - 1], fill="white")
    return im.resize((size, size), Image.LANCZOS)

# Legacy launcher PNGs (API 23-25)
for q, s in {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}.items():
    os.makedirs(f"{res}/mipmap-{q}", exist_ok=True)
    render(s).save(f"{res}/mipmap-{q}/ic_launcher.png", optimize=True)
# Google Play store icon: 512x512, full square (Play applies its own mask)
os.makedirs(out_preview, exist_ok=True)
render(512, rounded=False).save(f"{out_preview}/icon-512.png", optimize=True)
