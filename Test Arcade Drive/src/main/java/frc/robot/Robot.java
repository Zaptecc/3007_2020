/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

/**
 * This is a demo program showing the use of the DifferentialDrive class.
 * Runs the motors with arcade steering.
 */
public class Robot extends TimedRobot {

  private static DifferentialDrive m_robotDrive;
  private final Joystick m_stick = new Joystick(0);

  @Override
  public void robotInit() {

  Spark m_frontLeft = new Spark(1);
  Spark m_rearLeft = new Spark(0);
  SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);

  Spark m_frontRight = new Spark(3);
  Spark m_rearRight = new Spark(2);
  SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);

  m_robotDrive = new DifferentialDrive(m_left, m_right);
  m_robotDrive.setSafetyEnabled(false);
  m_robotDrive.setExpiration(0.1);

  }

  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    m_robotDrive.arcadeDrive(-1*m_stick.getY(), m_stick.getTwist());
  }
}
