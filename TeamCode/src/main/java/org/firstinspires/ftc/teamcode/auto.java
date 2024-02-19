package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

@Autonomous(name = "auto")
public class auto extends LinearOpMode {
    static final double HD_COUNTS_PER_REV = 28;
    static final double DRIVE_GEAR_REDUCTION = 20.15293;
    static final double WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
    static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
    static final double clicksPerDeg = 21.94;
    private static final boolean USE_WEBCAM = true;
    private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/myCustomModel.tflite";
    private static final String[] LABELS = {
            "Pixel",
    };
    int lfPos, rfPos, lrPos, rrPos;
    private TfodProcessor tfod;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;
    private Servo plane;
    private DcMotor rollIn;
    private DcMotor dualArm;
    private Servo garbageCollector;
    private DistanceSensor distance;

    private void moveForward(int howMuch, double speed) {
        // howMuch is in mm. A negative howMuch moves backward.

        // fetch motor positions
        lfPos = frontLeftMotor.getCurrentPosition();
        rfPos = frontRightMotor.getCurrentPosition();
        lrPos = backLeftMotor.getCurrentPosition();
        rrPos = backRightMotor.getCurrentPosition();

        // calculate new targets
        lfPos += howMuch * DRIVE_COUNTS_PER_MM;
        rfPos += howMuch * DRIVE_COUNTS_PER_MM;
        lrPos += howMuch * DRIVE_COUNTS_PER_MM;
        rrPos += howMuch * DRIVE_COUNTS_PER_MM;

        // move robot to new position
        frontLeftMotor.setTargetPosition(lfPos);
        frontRightMotor.setTargetPosition(rfPos);
        backLeftMotor.setTargetPosition(lrPos);
        backRightMotor.setTargetPosition(rrPos);
        frontLeftMotor.setPower(speed);
        frontRightMotor.setPower(speed);
        backLeftMotor.setPower(speed);
        backRightMotor.setPower(speed);

        // wait for move to complete
        while (frontLeftMotor.isBusy() && frontRightMotor.isBusy() &&
                backLeftMotor.isBusy() && backRightMotor.isBusy()) {

            // Display it for the driver.
            telemetry.addLine("Move Foward");
            telemetry.addData("Target", "%7d :%7d", lfPos, rfPos, lrPos, rrPos);
            telemetry.addData("Actual", "%7d :%7d", frontLeftMotor.getCurrentPosition(),
                    frontRightMotor.getCurrentPosition(), backLeftMotor.getCurrentPosition(),
                    backRightMotor.getCurrentPosition());
            telemetry.update();
        }

        // Stop all motion;
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    private void moveRight(int howMuch, double speed) {
        // howMuch is in mm. A negative howMuch moves backward.

        // fetch motor positions
        lfPos = frontLeftMotor.getCurrentPosition();
        rfPos = frontRightMotor.getCurrentPosition();
        lrPos = backLeftMotor.getCurrentPosition();
        rrPos = backRightMotor.getCurrentPosition();

        // calculate new targets
        lfPos += howMuch * DRIVE_COUNTS_PER_MM;
        rfPos -= howMuch * DRIVE_COUNTS_PER_MM;
        lrPos -= howMuch * DRIVE_COUNTS_PER_MM;
        rrPos += howMuch * DRIVE_COUNTS_PER_MM;

        // move robot to new position
        frontLeftMotor.setTargetPosition(lfPos);
        frontRightMotor.setTargetPosition(rfPos);
        backLeftMotor.setTargetPosition(lrPos);
        backRightMotor.setTargetPosition(rrPos);
        frontLeftMotor.setPower(speed);
        frontRightMotor.setPower(speed);
        backLeftMotor.setPower(speed);
        backRightMotor.setPower(speed);

        // wait for move to complete
        while (frontLeftMotor.isBusy() && frontRightMotor.isBusy() &&
                backLeftMotor.isBusy() && backRightMotor.isBusy()) {

            // Display it for the driver.
            telemetry.addLine("Strafe Right");
            telemetry.addData("Target", "%7d :%7d", lfPos, rfPos, lrPos, rrPos);
            telemetry.addData("Actual", "%7d :%7d", frontLeftMotor.getCurrentPosition(),
                    frontRightMotor.getCurrentPosition(), backLeftMotor.getCurrentPosition(),
                    backRightMotor.getCurrentPosition());
            telemetry.update();
        }

        // Stop all motion;
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);

    }

    private void turnClockwise(int whatAngle, double speed) {
        // whatAngle is in degrees. A negative whatAngle turns counterclockwise.

        // fetch motor positions
        lfPos = frontLeftMotor.getCurrentPosition();
        rfPos = frontRightMotor.getCurrentPosition();
        lrPos = backLeftMotor.getCurrentPosition();
        rrPos = backRightMotor.getCurrentPosition();

        // calculate new targets
        lfPos += whatAngle * clicksPerDeg;
        rfPos -= whatAngle * clicksPerDeg;
        lrPos += whatAngle * clicksPerDeg;
        rrPos -= whatAngle * clicksPerDeg;

        // move robot to new position
        frontLeftMotor.setTargetPosition(lfPos);
        frontRightMotor.setTargetPosition(rfPos);
        backLeftMotor.setTargetPosition(lrPos);
        backRightMotor.setTargetPosition(rrPos);
        frontLeftMotor.setPower(speed);
        frontRightMotor.setPower(speed);
        backLeftMotor.setPower(speed);
        backRightMotor.setPower(speed);

        // wait for move to complete
        while (frontLeftMotor.isBusy() && frontRightMotor.isBusy() &&
                backLeftMotor.isBusy() && backRightMotor.isBusy()) {

            // Display it for the driver.
            telemetry.addLine("Turn Clockwise");
            telemetry.addData("Target", "%7d :%7d", lfPos, rfPos, lrPos, rrPos);
            telemetry.addData("Actual", "%7d :%7d", frontLeftMotor.getCurrentPosition(),
                    frontRightMotor.getCurrentPosition(), backLeftMotor.getCurrentPosition(),
                    backRightMotor.getCurrentPosition());
            telemetry.update();
        }

        // Stop all motion;
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    /**
     * Describe this function...
     */
    private void roll() {
        rollIn.setPower(0);
        if (gamepad1.left_bumper) {
            rollIn.setDirection(DcMotorSimple.Direction.FORWARD);
            rollIn.setPower(1);
        }
        if (gamepad1.right_bumper) {
            rollIn.setDirection(DcMotorSimple.Direction.REVERSE);
            rollIn.setPower(1);
        }
    }

    /**
     * Describe this function...
     */
    private void armUp_Down() {
        dualArm.setPower(0);
        if (gamepad1.a) {
            dualArm.setDirection(DcMotorSimple.Direction.REVERSE);
            dualArm.setPower(0.3);
        }
        if (gamepad1.y) {
            dualArm.setDirection(DcMotorSimple.Direction.FORWARD);
            dualArm.setPower(0.3);
        }
    }

    private void collectGarbage() {
        if (gamepad1.left_trigger > 0) {
            garbageCollector.setPosition(0);
        }
        if (gamepad1.right_trigger > 0) {
            garbageCollector.setPosition(1);
        }
    }

    private void telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double y = (recognition.getTop() + recognition.getBottom()) / 2;

            telemetry.addData("", " ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method telemetryTfod()

    private void telemetryAprilTag() {

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");

    }   // end method telemetryAprilTag()

    private void InitVision() {
        aprilTag = new AprilTagProcessor.Builder()

                .build();

        tfod = new TfodProcessor.Builder()
                .build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        builder.addProcessors(aprilTag, tfod);

    }

    @Override
    public void runOpMode() {
        InitVision();

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        rollIn = hardwareMap.get(DcMotor.class, "rollIn");
        dualArm = hardwareMap.get(DcMotor.class, "dualArm");
        garbageCollector = hardwareMap.get(Servo.class, "garbageCollector");
        distance = hardwareMap.get(DistanceSensor.class, "Distance");

        // Put initialization blocks here.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.

            visionPortal.close();
        }
    }
}