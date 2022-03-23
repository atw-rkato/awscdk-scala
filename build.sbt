import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtConfig

lazy val root = (project in file("."))
  .settings(
    organization := "com.myorg",
    name := "awscdk-scala-samples",
    version := "0.1",
    scalaVersion := "2.13.8",
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Wconf:cat=lint-byname-implicit:s",
    ),
    javacOptions ++= Seq("-source", "11", "-target", "11", "-encoding", "UTF-8"),
    scalafmtConfig := file(".scalafmt.conf"),
    libraryDependencies ++= cdkDependencies ++ testDependencies,
  )

// https://mvnrepository.com/artifact/software.amazon.awscdk/core
lazy val cdkVersion = "1.149.0"
lazy val cdkDependencies = Seq(
  "software.amazon.awscdk" % "core"                           % cdkVersion,
  "software.amazon.awscdk" % "elasticloadbalancingv2"         % cdkVersion,
  "software.amazon.awscdk" % "elasticloadbalancingv2-targets" % cdkVersion,
  "software.amazon.awscdk" % "ec2"                            % cdkVersion,
  "software.amazon.awscdk" % "rds"                            % cdkVersion,
  //  "software.amazon.awscdk" % "autoscaling"                    % cdkVersion,
  //  "software.amazon.awscdk" % "apigateway"                     % cdkVersion,
  //  "software.amazon.awscdk" % "dynamodb"                       % cdkVersion,
  //  "software.amazon.awscdk" % "lambda"                         % cdkVersion,
//  "software.amazon.awscdk" % "codebuild"                      % cdkVersion,
  //  "software.amazon.awscdk" % "cloudformation"                 % cdkVersion,
  //  "software.amazon.awscdk" % "ecs"                            % cdkVersion,
  //  "software.amazon.awscdk" % "ecs-patterns"                   % cdkVersion,
  //  "software.amazon.awscdk" % "sns"                            % cdkVersion,
  //  "software.amazon.awscdk" % "sqs"                            % cdkVersion,
  //  "software.amazon.awscdk" % "sns-subscriptions"              % cdkVersion,
  //  "software.amazon.awscdk" % "events-targets"                 % cdkVersion,
  //  "software.amazon.awscdk" % "cloudfront"                     % cdkVersion,
  //  "software.amazon.awscdk" % "route53"                        % cdkVersion,
  //  "software.amazon.awscdk" % "route53-targets"                % cdkVersion,
  "software.amazon.awscdk" % "s3" % cdkVersion,
  //  "software.amazon.awscdk" % "s3-deployment"                  % cdkVersion,
  //  "software.amazon.awscdk" % "certificatemanager"             % cdkVersion,
  //  "software.amazon.awscdk" % "stepfunctions"                  % cdkVersion,
  //  "software.amazon.awscdk" % "stepfunctions-tasks"            % cdkVersion,
)

lazy val testDependencies = Seq(
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.scalatest"     %% "scalatest" % "3.2.11",
).map { _ % Test }
