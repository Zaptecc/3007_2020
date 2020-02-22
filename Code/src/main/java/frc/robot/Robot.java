/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import com.revrobotics.CANError;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // VARIABLES

  // CIMS
  private static final int kFrontLeftChannel = 1;
  private static final int kRearLeftChannel = 0;
  private static final int kFrontRightChannel = 3;
  private static final int kRearRightChannel = 2;

  private static final int kWinch = 5;

  // GEARMOTORS
  private static final int kCtrlPanel = 10;
  private static final int kGondolla = 9;

  private static final int kBallTube1 = 6;
  private static final int kBallTube2 = 8;

  // BRUSHLESS

  private static final int kBallIntake1 = 2;
  private static final int kBallIntake2 = 4;

  private static final int kBallShooter = 1;

  // SOLENOIDS
  private static final int kBallStop = 3;
  private static final int kShifter = 1;
  private static final int kGatherLift = 0;
  private static final int kCtrlPanelLifter = 4;
  private static final int kClimbBrake = 2;

  // JOYSTICKS
  private static final int kDriveStick = 0;
  private static final int kControlStick = 1;

  private static final String kCenterAuto = "Center";
  private static final String kLeftAuto = "Left";
  private static final String kRightAuto = "Right";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private static DifferentialDrive m_robotDrive;

  // Input Devices
  private static Joystick driveStick;
  private static Joystick controlStick;

  // Motors

  private static CANSparkMax ballIntake1;
  private static CANSparkMax ballIntake2;
  private static CANSparkMax ballShooter;

  private static CANEncoder shooterEncoder;

  private static VictorSPX winch;
  private static VictorSPX ctrlPanel;
  private static TalonSRX ballTube1;
  private static VictorSPX ballTube2;
  private static TalonSRX gondolla;

  // Solenoids
  private static Solenoid ballStop;
  private static Solenoid shifter;
  private static Solenoid gatherLift;
  private static Solenoid climbBrake;
  private static Solenoid ctrlPanelLifter;

  // Pneumatics
  private static Compressor compressor;

  public static DifferentialDrive getDrive() {
    return m_robotDrive;
  }

  public static Joystick getJoystick() {
    return driveStick;
  }

  public static Joystick getJoystick2() {
    return controlStick;
  }

  public static Timer timer;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {

    // Auto Choice Init
    m_chooser.setDefaultOption("Center (Default)", kCenterAuto);
    m_chooser.addOption("Left", kLeftAuto);
    m_chooser.addOption("Right", kRightAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    // Drivetrain Init
    Spark m_frontLeft = new Spark(kFrontLeftChannel);
    Spark m_rearLeft = new Spark(kRearLeftChannel);
    SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);

    Spark m_frontRight = new Spark(kFrontRightChannel);
    Spark m_rearRight = new Spark(kRearRightChannel);
    SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);

    m_robotDrive = new DifferentialDrive(m_left, m_right);
    m_robotDrive.setSafetyEnabled(false);
    m_robotDrive.setExpiration(0.1);

    // m_rearRight.setInverted(true);
    // m_frontRight.setInverted(true);

    // Other Motor Init
    ballIntake1 = new CANSparkMax(kBallIntake1, MotorType.kBrushless);
    ballIntake2 = new CANSparkMax(kBallIntake2, MotorType.kBrushless);
    ballShooter = new CANSparkMax(kBallShooter, MotorType.kBrushless);
      shooterEncoder = new CANEncoder(ballShooter);

    ctrlPanel = new VictorSPX(kCtrlPanel);
    winch = new VictorSPX(kWinch);

    ballTube1 = new TalonSRX(kBallTube1);
    ballTube2 = new VictorSPX(kBallTube2);
    gondolla = new TalonSRX(kGondolla);

    // Solenoids
    ballStop = new Solenoid(kBallStop);
    climbBrake = new Solenoid(kClimbBrake);
    ctrlPanelLifter = new Solenoid(kCtrlPanelLifter);
    shifter = new Solenoid(kShifter);
    gatherLift = new Solenoid(kGatherLift);

    // Pneumatics
    compressor = new Compressor();

    compressor.start();

    // Joystick Init
    driveStick = new Joystick(kDriveStick);
    controlStick = new Joystick(kControlStick);

    timer = new Timer();

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    timer.reset();
    timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCenterAuto:
      if (timer.get() > 2.0) {
        m_robotDrive.arcadeDrive(0.35, 0);
      } else {
        m_robotDrive.arcadeDrive(0, 0);
      }
      break;
    case kLeftAuto:
    default:
      if (timer.get() > 2.0) {
        m_robotDrive.arcadeDrive(0.35, 0);
      } else {
        m_robotDrive.arcadeDrive(0, 0);
      }
      break;
    case kRightAuto:
      if (timer.get() > 2.0) {
        m_robotDrive.arcadeDrive(0.35, 0);
      } else {
        m_robotDrive.arcadeDrive(0, 0);
      }
      break;
    }
  }

  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    //double xval = driveStick.getX();
    double yval = -0.7 * driveStick.getY();
    double twistval = 0.7 * driveStick.getTwist();

    if (driveStick.getRawButton(1) == true) {
      yval = 1 * driveStick.getY();
      twistval = 0.7 * driveStick.getTwist();
    } else {
      yval = 0.7 * driveStick.getY();
      twistval = 0.7 * driveStick.getTwist();
    }
    ;

    m_robotDrive.arcadeDrive(yval, twistval);

    //gatherLift.set(true);
    // ctrlPanelLifter.set(true);
    climbBrake.set(true);

    if (controlStick.getRawButtonPressed(7) == true) {

      //Turn on intake
      gatherLift.set(true);
      ballStop.set(false);
      //ballIntake1.set(0.1);
      ballIntake2.set(-0.2);

      //Delay for half a second to allow ball stop pneumatics
      //to deploy, then turn on ball tube
      //if (timer.get() == 0.5) {
        ballTube1.set(ControlMode.PercentOutput, -0.45);
        ballTube2.set(ControlMode.PercentOutput, -0.55);
      //}

    }

    if (controlStick.getRawButtonPressed(8) == true) {

      //Reverse ball tube for a short period to prevent damage to
      //ball stop pneumatics
      //ballTube1.set(ControlMode.PercentOutput, 0.55);
      //ballTube2.set(ControlMode.PercentOutput, 0.65);

      //if(timer.get() == 0.2) {
        ballStop.set(true);
        //ballIntake1.set(0.1);
        ballIntake2.set(0);
        ballTube1.set(ControlMode.PercentOutput, 0);
        ballTube2.set(ControlMode.PercentOutput, 0);
      //}

    }

    if(controlStick.getRawButton(1) == true) {
      ballStop.set(true);
      //ballShooter.set(-0.75);
      SmartDashboard.putNumber("Shooter Speed (RPM): ", shooterEncoder.getVelocity());


      if (shooterEncoder.getVelocity() <= -3600 ) {
        ballTube1.set(ControlMode.PercentOutput, -0.55);
        ballTube2.set(ControlMode.PercentOutput, -0.65);
      } else {
        ballTube1.set(ControlMode.PercentOutput, 0.0);
        ballTube2.set(ControlMode.PercentOutput, 0.0);
        ballShooter.set(ballShooter.get() - 0.005);
      };

    };

    if(controlStick.getRawButtonPressed(3) == true) {
      ballShooter.set(0.77);
    };

    if(controlStick.getRawButton(4) == true) {
      ballShooter.set(0.0);
    };

    if(controlStick.getRawButton(5) == true) {
      winch.set(ControlMode.PercentOutput, controlStick.getY());
    } else {
      winch.set(ControlMode.PercentOutput, 0.0);
    }

    if(controlStick.getRawButton(6) == true) {
      gondolla.set(ControlMode.PercentOutput, controlStick.getY());
    } else {
      gondolla.set(ControlMode.PercentOutput, 0.0);
    };


    if(controlStick.getRawButtonPressed(10) == true) {
      ballStop.set(!ballStop.get());
    };

    if(controlStick.getRawButtonPressed(11) == true) {
      gatherLift.set(!gatherLift.get());
    };

    if(controlStick.getRawButton(12) == true) {
      ctrlPanel.set(ControlMode.PercentOutput, controlStick.getY());
    } else {
      ctrlPanel.set(ControlMode.PercentOutput, 0.0);
    };

    if(driveStick.getRawButtonPressed(12) == true) {
      ctrlPanelLifter.set(!ctrlPanelLifter.get());
    };

    /*if(driveStick.getRawButtonPressed(7) == true) {
      climbBrake.set(!climbBrake.get());
    };*/

    if(driveStick.getRawButtonPressed(7) == true) {

    };


  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
