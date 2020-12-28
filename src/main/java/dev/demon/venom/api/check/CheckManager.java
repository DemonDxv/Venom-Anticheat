package dev.demon.venom.api.check;

/*
public class CheckManager {

    public static final Class[] checks = new Class[] {

            //Combat
            AimAssistA.class,
            AimAssistB.class,
            AimAssistC.class,
          //  AimAssistD.class,
          //  AimAssistE.class,

            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoClickerF.class,
            AutoClickerG.class,
            AutoClickerH.class,
            AutoClickerI.class,
            AutoClickerJ.class,
            AutoClickerK.class,

            KillauraA.class,
            KillauraB.class,
            KillauraC.class,
            KillauraD.class,
            KillauraE.class,

         //   KillauraF.class,
            KillauraG.class,
            KillauraH.class,
            KillauraI.class,
            KillauraJ.class,
            KillauraK.class,
            KillauraL.class,
            KillauraM.class,

            ReachA.class,
         //   ReachB.class,

            VelocityA.class,
          //  VelocityB.class,
            VelocityC.class,
            VelocityD.class,
            VelocityE.class,
        //    VelocityF.class,
            VelocityG.class,
          //  VelocityH.class,

            //Movement
        //    SpeedA.class,
          //  SpeedB.class,

            MotionA.class,
           // MotionB.class,

            FlyA.class,
            FlyB.class,
            FlyC.class,
            FlyD.class,
            FlyE.class,



            //Other

          //  PhaseA.class,

            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
        //    BadPacketsG.class,
            BadPacketsH.class,
            BadPacketsI.class,
            BadPacketsJ.class,
            BadPacketsK.class,
          //  BadPacketsL.class,
          //  BadPacketsM.class,
          //  BadPacketsN.class,
          //  BadPacketsO.class,
            BadPacketsP.class,
            BadPacketsQ.class,
            BadPacketsR.class,
            BadPacketsS.class,
          //  BadPacketsT.class,
            BadPacketsU.class,
            BadPacketsV.class,
        //    BadPacketsW.class,
        //    BadPacketsX.class,
            BadPacketsY.class,
            BadPacketsZ.class,

            BadPacketsA1.class,
            BadPacketsB1.class,
           // BadPacketsC1.class,
            BadPacketsD1.class,
            BadPacketsE1.class,
          //  BadPacketsF1.class,
            BadPacketsG1.class,

            TimerA.class,
    };



    public static List<Check> loadChecks() {
        List<Check> checklist = new ArrayList<>();
        Arrays.asList(checks).forEach(check -> {
            try {
                checklist.add((Check) check.getConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return checklist;
    }

    public static void updateCheckFreezeState(CheckData checkData) {
        String name = checkData.getName();
        String type = checkData.getType();

        if (checkData.getUser() != null) {
            Check fondCheck = checkData.getUser().getChecks().parallelStream().filter(check ->
                    check.getName().equalsIgnoreCase(name)
                    && check.getType().equalsIgnoreCase(type)).findAny().orElse(null);

            assert fondCheck != null;
            fondCheck.freeze = checkData.freeze;
        }
    }

    public <T> Check forClass(Class<? extends Check> aClass, User user) {
        return (Check) user.getChecks().stream()
                .filter(module -> module.getClass().equals(aClass)).findFirst()
                .orElse(null);
    }

    public static CheckInfo getCheckInfo(Check check) {
        return check.getClass().getAnnotation(CheckInfo.class);
    }

    @Getter
    @AllArgsConstructor
    public static class CheckData {
        private String name, type;
        private boolean freeze;
        private User user;
    }
}
*/