import io
import base64

import cv2
import PIL
import flask
import numpy as np
from PIL import Image
from flask import request, jsonify

from config import *
from model import get_model_prediction

app = flask.Flask(__name__)


@app.route("/get_fuel_efficiency", methods=["GET"])
def main():
    img_base64_string = request.args.get("image")
    vehicle_make = request.args.get("vehicle_make")
    img = PIL.Image.fromarray(stringToRGB(img_base64_string))
    img = rgb2gray(np.array(img.resize((IMAGE_SIZE, IMAGE_SIZE), Image.ANTIALIAS)))
    img = cv2.GaussianBlur(img, (5, 5), 0)
    output = img.copy()
    cx, cy, ci = drawcircle(img, output)
    try:
        nr, cr, final = drawlines(img, ci, cx, cy)
        tyre_pressure_drop_percent = (((nr-cr)*RADII_CHANGE)/PRESSURE_CHANGE)*100
        fuel_efficiency_drop_percent = tyre_pressure_drop_percent*FUEL_EFF_DROP
        predicted_class = get_model_prediction(vehicle_make)
        result = {"state": ["SUCCESS"], "output": [str(fuel_efficiency_drop_percent), str(predicted_class)]}
        
    except Exception as e:
        result = {"state": ["404 FAILURE"],
                  "message": [f"Error in getting fuel efficiency: {e}"]}

    return jsonify(result)
    
    
# Take in base64 string and return cv image
def stringToRGB(base64_string):
    print(len(base64_string))
#    base64_bytes = str(base64_string).encode('ascii')
    imgdata = base64.b64decode(str(base64_string))
    print("imgdata", len(imgdata))
    image = Image.open(io.BytesIO(imgdata))
    return cv2.cvtColor(np.array(image), cv2.COLOR_BGR2RGB)


def rgb2gray(rgb):
 
    r, g, b = rgb[:,:,0], rgb[:,:,1], rgb[:,:,2]
    gray = 0.2989 * r + 0.5870 * g + 0.1140 * b
 
    return gray

    
def drawcircle(image, output):
    
    ret, thresh = cv2.threshold(image, 35, 255, 12)

    circles = cv2.HoughCircles(thresh, cv2.HOUGH_GRADIENT, 2, 150, param1=20, param2=150, minRadius=140, maxRadius=300)
    centre_x , centre_y = image.shape[1], image.shape[0]
    if circles is not None:
        circles = np.round(circles[0, :]).astype("int")
        for (x, y, r) in circles:
            cv2.circle(output, (x, y), r, (255, 0, 0), 4)
            centre_x = x
            centre_y = y
            cv2.rectangle(output, (x - 5, y - 5), (x + 5, y + 5), (255, 0, 0), -1)
        #display(output)
    return centre_x, centre_y, output


def drawlines(image, output, centre_x, centre_y):
    im_size = image.shape
    print("im_size", im_size)
    b = False
    
    for i in range(im_size[0]-1, 0, -1):
        for j in range(0, im_size[1]):
            a = image[i, j]
            if a < 50 and j > im_size[1]*0.25 and j < im_size[1]*0.75:
                line_y = i # dec
                line_x = j # inc
                b = True
                break
        if b:
            break

    cv2.line(output,(0, line_y),(im_size[1], line_y),(255,255,255),1)
    compressed_radius = line_y - centre_y
    
    for i in range(centre_x-compressed_radius , 0, -1):
        a = image[centre_y, i]
        if a > 75 or a < 30:
            line2_x = i
            break
        
    for i in range(centre_x+compressed_radius, im_size[1]):
        a = image[centre_y, i]
#         print(a)
        if a > 75 or a < 20:
            line3_x = i
            break
    
    normal_radius = (line3_x - line2_x) / 2

    cv2.line(output,(line2_x, 0),(line2_x, im_size[0]),(255,255,255),1)
    cv2.line(output,(line3_x, 0),(line3_x, im_size[0]),(255,255,255),1)
    
    return normal_radius, compressed_radius, output


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5444)
