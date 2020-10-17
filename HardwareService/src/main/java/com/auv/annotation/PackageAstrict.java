package com.auv.annotation;

/**
 * @author LiChuang
 * @since 2020/6/23 10:48
 **/
public @interface PackageAstrict {


        @interface  Common{

               String  COM  = "com";

               String  NET  = "net";

        }



    /**
     * 哎呦喂
     */
     @interface  AUV{
        /*
              二级包名
         */
         @interface Second{
            String  SECOND_PGE1  = "aiyouwei";

            String  SECOND_PGE2  = "auv";
         }




        /*
               三级报名
            */
        @interface Third{

            String  THIRD_PGE1 = "auvandroid";

            String  THIRD_PGE2 = "hardwaretest";
        }



    }


    /**
     * 科拜斯
     */
    @interface  KBS{

        /*
       二级包名
     */
        @interface Second{

            String  SECOND_PGE  = "rfidstar";
        }



        /*
               三级报名
            */
        @interface Third{
            String  THIRD_PGE  = "insulationcabinet";
        }


    }


}
