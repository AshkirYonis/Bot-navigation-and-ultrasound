Robot Movement and ultrasound detection

Overview
The SwiftBot Navigation System is a Java-based application designed to control a SwiftBot using commands received from QR codes and ultrasound detection. The project demonstrates the integration of image processing, command parsing, and real-time robotics control.

Features
QR Code Scanning: Utilizes the SwiftBot API to scan and decode QR codes that contain navigation commands.
Ultrasound Detection: Measures the distance to obstacles in front of the SwiftBot using ultrasound sensors.
Command Execution: Interprets commands for movement, including forward, backward, turning left, and turning right, based on parameters like duration and speed.
Movement Retracing: Records executed commands and allows the SwiftBot to retrace its movements based on user input.
Command Logging: Maintains a log of all executed commands, which can be saved to a text file for review.


