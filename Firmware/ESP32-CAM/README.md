
# ESP32-CAM AI Thinker module

## Configuration url:

You can set the general configuration of the camera by accesing the url at: http://xx.xx.xx.xx on the network (replace with your camera ip). The specific IP can be set on the `CameraWebServer.ino` code in the line `IPAddress ip(xx, xx, xx, xx)`. Remember to configure the subnet mask and default gateway accordingly

## Stream url:

The stream url can be found at http://xx.xx.xx.xx:81/stream this stream is affected by the configuration set in the configuration url

## Seppeding up the stream:

Different things can be used to speed up the stream

- Use more than 1 frame buffer count: As the board has external PSRAM 2 fram buffers can be used this is set in the `CameraWebServer.ino` code in the line `config.fb_count = 2;`
- Configure the buffer to only have the latest frames: this can be configured in the `CameraWebServer.ino` code in the line `config.grab_mode = CAMERA_GRAB_LATEST;`


