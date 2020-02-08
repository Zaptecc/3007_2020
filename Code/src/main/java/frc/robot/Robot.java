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
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {


                //VARIABLES

    //CIMS
  private static final int kFrontLeftChannel = 1;
  private static final int kRearLeftChannel = 0;
  private static final int kFrontRightChannel = 3;
  private static final int kRearRightChannel = 2;

  private static final int kWinchChannel = 6;

    //GEARMOTORS
  private static final int kWOFSpinner = 9;

  private static final int kBallTube1 = 6;
  private static final int kBallTube2 = 6;

  private static final int kBallIntake1 = 6;
  private static final int kBallIntake2 = 6;

    //BRUSHLESS
  private static final int kBallThrower = 6;

    //JOYSTICKS
  private static final int kDriveStick = 0;
  private static final int kControlStick = 0; //1


  private static final String kCenterAuto = "Center";
  private static final String kLeftAuto = "Left";
  private static final String kRightAuto = "Right";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private static DifferentialDrive m_robotDrive;

  //Input Devices
  private static Joystick driveStick;
  private static Joystick controlStick;

  public static DifferentialDrive getDrive() {
    return m_robotDrive;
  }

  public static Joystick getJoystick() {
    return driveStick;
  }

  public static Joystick getJoystick2() {
    return driveStick; //controlStick;
  }

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

        //Auto Choice Init
    m_chooser.setDefaultOption("Center (Default)", kCenterAuto);
    m_chooser.addOption("Left", kLeftAuto);
    m_chooser.addOption("Right", kRightAuto);
    SmartDashboard.putData("Auto choices", m_chooser);


        //Drivetrain Init
    Spark m_frontLeft = new Spark(kFrontLeftChannel);
    Spark m_rearLeft = new Spark(kRearLeftChannel);
    SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);

    Spark m_frontRight = new Spark(kFrontRightChannel);
    Spark m_rearRight = new Spark(kRearRightChannel);
    SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);

    m_robotDrive = new DifferentialDrive(m_left, m_right);
    m_robotDrive.setSafetyEnabled(false);
    m_robotDrive.setExpiration(0.1);

    //m_rearRight.setInverted(true);
    //m_frontRight.setInverted(true);

        //Other Motor Init
    VictorSP WOFSpinner = new VictorSP(kWOFSpinner);

    //Joystick Init
    driveStick = new Joystick(kDriveStick);
    controlStick = new Joystick(kControlStick);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCenterAuto:
        // Put center auto code here
        break;
      case kLeftAuto:
      default:
        // Put left auto code here
        break;
      case kRightAuto:
        //Put right auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    double xval = driveStick.getX();
    double yval = -0.7*driveStick.getY();
    double twistval = 0.7*driveStick.getTwist();

    if(controlStick.getRawButton(1) == true){

      yval = -1*driveStick.getY();
      twistval = 0.7*driveStick.getTwist();

    }else{

      yval = -0.7*driveStick.getY();
      twistval = 0.7*driveStick.getTwist();

    };

    m_robotDrive.arcadeDrive(yval, twistval);

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
