package org.usfirst.frc.team1806.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team1806.robot.Constants;
import org.usfirst.frc.team1806.robot.auto.AutoModeBase;
import org.usfirst.frc.team1806.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team1806.robot.auto.actions.*;
import org.usfirst.frc.team1806.robot.auto.actions.intakeaction.IntakeCube;
import org.usfirst.frc.team1806.robot.auto.actions.intakeaction.SpitOutCube;
import org.usfirst.frc.team1806.robot.auto.actions.liftactions.*;
import org.usfirst.frc.team1806.robot.auto.paths.*;
import org.usfirst.frc.team1806.robot.auto.paths.qual.LR.DriveToSwitch;
import org.usfirst.frc.team1806.robot.auto.paths.qual.LR.SwitchToCube;
import org.usfirst.frc.team1806.robot.auto.paths.qual.RL.ScaleToSwitch;
import org.usfirst.frc.team1806.robot.auto.paths.scaletoblock.leftside.LeftSideScalePart1;
import org.usfirst.frc.team1806.robot.auto.paths.scaletoblock.rightside.RightSideScaleToBlockPart1;
import org.usfirst.frc.team1806.robot.auto.paths.threecube.ThreeCubePart1;
import org.usfirst.frc.team1806.robot.auto.paths.threecube.ThreeCubePart2;
import org.usfirst.frc.team1806.robot.auto.paths.threecube.ThreeCubePart3;
import org.usfirst.frc.team1806.robot.path.PathContainer;
import org.usfirst.frc.team1806.robot.util.Translation2d;

import java.util.Arrays;

public class CoOp1710 extends AutoModeBase {
	/**
	 * Qual Mode will be what we use for almost every qual match
	 *
	 * This auto decides which side to run on then drops off a cube at both the scale and the switch
	 * @throws AutoModeEndedException
	 */
	@Override
	protected void routine() throws AutoModeEndedException {
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage().toUpperCase().substring(0, 2);
		if(gameData.equals("RR")) {
			PathContainer pickupCube = new PickupCube();
			runAction(new ResetPoseFromPathAction(pickupCube));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new DrivePathAction(pickupCube),
//							new IntakeCube()
					}
			)));
		} else if(gameData.equals("LL")) {
			PathContainer safeSide = new LeftSideSafe();
			runAction(new ResetPoseFromPathAction(safeSide));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new SeriesAction(Arrays.asList(
									new LiftToTeleopHold(),
									new RunActionAtX(260, new RunActionAtLiftHeight(Constants.kHighScaleSpitOutCount, (new SpitOutCube(.1))))
							)),
							new DrivePathAction(safeSide),
							new RunActionAtX(100, new LiftToHighScale(false)),
					}
			)));
			runAction(new OutputTime("Finished Left Side"));
			runAction(new LiftToBottom(true));
			runAction(new TurnTowardsPoint(new Translation2d(223,220)));
			runAction(new OutputTime("Finished Turn"));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new IntakeCube(),
							new DrivePathAction(new LeftSideScalePart1()),
					})));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new LiftToSwitch(),
							new RunActionAtLiftHeight(Constants.kSwitchEncoderSpit, new SpitOutCube(.1))
					}
			)));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new DrivePathAction(new ThreeCubePart1()),
							new LiftToBottom(true)
					}
			)));

			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new IntakeCube(),
							new DrivePathAction(new ThreeCubePart2())
					})));
			runAction(new DrivePathAction(new ThreeCubePart3()));

			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new TurnTowardsPoint(new Translation2d(300, 210)),
							new LiftToNeutralScale(),
							new RunActionAtLiftHeight(Constants.kNeutralScaleSpitOutCount, new SpitOutCube(.1))
					})));

		} else if(gameData.equals("LR")) {
			PathContainer safeSide = new DriveToSwitch();
			runAction(new ResetPoseFromPathAction(safeSide));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new DrivePathAction(safeSide),
							new LiftToSwitch(),
							new RunActionAtX(110, new SpitOutCube(.1))
                    })));
            runAction(new ParallelAction(Arrays.asList(
                    new Action[]{
                            new TurnTowardsPoint(new Translation2d(170,300)),
                            new LiftToBottom(true)
                    })));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new SeriesAction(Arrays.asList(
									new Action[]{
											new RunActionAtX(130, new LiftToTeleopHold()),
											new RunActionAtY(160, new LiftToBottom(true))
									}
							)),
							new DrivePathAction(new SwitchToCube()),
							new RunActionAtY(150, new IntakeCube())

					})));

			runAction(new LiftToHighScale(false));
			runAction(new RunActionAtLiftHeight(18000, new SpitOutCube(.1)));
		} else if(gameData.equals("RL")) {
			PathContainer safeSide = new LeftSideSafe();
			runAction(new ResetPoseFromPathAction(safeSide));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new SeriesAction(Arrays.asList(
									new LiftToTeleopHold(),
									new RunActionAtX(263, new RunActionAtLiftHeight(Constants.kHighScaleSpitOutCount, (new SpitOutCube(.1))))
							)),
							new DrivePathAction(safeSide),
							new RunActionAtX(100, new LiftToHighScale(false)),
					}
			)));
			runAction(new OutputTime("Finished Left Side"));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new TurnTowardsPoint(new Translation2d(190,210)),
							new RunActionAtAngleRange(-180,-15,new LiftToBottom(true))
					}
			)));
			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new DrivePathAction(new ScaleToSwitch()),
							new RunActionAtY(130, new LiftToBottom(true))
					}
			)));
			runAction(new TurnTowardsPoint(new Translation2d(200,70)));

			runAction(new ParallelAction(Arrays.asList(
					new Action[]{
							new DrivePathAction(new UpOneFootRR(237,75,-15,false)),
							new IntakeCube()
					}
			)));
			runAction(new LiftToSwitch());
			runAction(new SpitOutCube(.1));
			runAction(new LiftToBottom(true));
		} else {
			runAction(new DrivePathAction(new DumbMode()));
		}
	}

}
