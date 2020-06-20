package cs.hku.wallpaper_sdk.abc;

public class zhuanbi {
    public static boolean getRotationMatrix(float[] R, float[] I,
                                            float[] gravity, float[] geomagnetic) {
        // TODO: move this to native code for efficiency
        /**
         * 重力沿【手机坐标系】三个轴的分量。
         * 但合力一定是竖直【向下】的，也就是地球坐标系的Z的反方向。
         * （即地球坐标系的-Z方向）
         */
        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];
        final float normsqA = (Ax * Ax + Ay * Ay + Az * Az);//求重力合力
        final float g = 9.81f;
        final float freeFallGravitySquared = 0.01f * g * g;
        if (normsqA < freeFallGravitySquared) {
            // gravity less than 10% of normal value
            return false;//重力太小。
        }
        /**
         * 当前磁场的大小，沿【手机坐标系】三个轴的分量。
         * 虽然磁场的水平分量是指北的，但总分量并不指北。
         */
        final float Ex = geomagnetic[0];
        final float Ey = geomagnetic[1];
        final float Ez = geomagnetic[2];

        /**
         * 磁力与重力的叉乘。
         * 得到垂直于【地球坐标系】-Z与磁场方向所构成的平面的向量，
         * 这个向量是指西的，即【地球坐标系】向量-X的方向。
         */
        float Hx = Ey * Az - Ez * Ay;
        float Hy = Ez * Ax - Ex * Az;
        float Hz = Ex * Ay - Ey * Ax;
        final float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);
        if (normH < 0.1f) {
            // device is close to free fall (or in space?), or close to
            // magnetic north pole. Typical values are  > 100.
            return false;//失重，在太空，或者手机在地磁的北极（站在地磁北极向下的重力为0）。
        }

        //单位化【地球坐标系】-X
        final float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;


        //单位化【地球坐标系】-Z
        final float invA = 1.0f / (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        Ax *= invA;
        Ay *= invA;
        Az *= invA;

        /** 【地球坐标系】单位化-z与-x的叉乘，
         *   得到【地球坐标系】单位的Y，这个Y才是真正的指北。
         *
         */
        final float Mx = Ay * Hz - Az * Hy;
        final float My = Az * Hx - Ax * Hz;
        final float Mz = Ax * Hy - Ay * Hx;

        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx;     R[1] = Hy;     R[2] = Hz;//【地球坐标系】-X方向，指西
                R[3] = Mx;     R[4] = My;     R[5] = Mz;//【地球坐标系】Y方向，指北
                R[6] = Ax;     R[7] = Ay;     R[8] = Az;//【地球坐标系】-Z方向，向下
            } else if (R.length == 16) {//4x4的是坐标平移的情况，我们在此不予考虑。
                R[0]  = Hx;    R[1]  = Hy;    R[2]  = Hz;   R[3]  = 0;
                R[4]  = Mx;    R[5]  = My;    R[6]  = Mz;   R[7]  = 0;
                R[8]  = Ax;    R[9]  = Ay;    R[10] = Az;   R[11] = 0;
                R[12] = 0;     R[13] = 0;     R[14] = 0;    R[15] = 1;
            }
        }
        if (I != null) {
            // compute the inclination matrix by projecting the geomagnetic
            // vector onto the Z (gravity) and X (horizontal component
            // of geomagnetic vector) axes.
            final float invE = 1.0f / (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
            final float c = (Ex * Mx + Ey * My + Ez * Mz) * invE;//【地球坐标系】Y与磁场方向夹角余弦
            final float s = (Ex * Ax + Ey * Ay + Ez * Az) * invE;//【地球坐标系】-Z与磁场方向夹角余弦
            if (I.length == 9) {//由以上两个公式，求出了当地磁场方向分别与竖直、水平两个分量的夹角。
                I[0] = 1;     I[1] = 0;     I[2] = 0;
                I[3] = 0;     I[4] = c;     I[5] = s;
                I[6] = 0;     I[7] = -s;     I[8] = c;
            } else if (I.length == 16) {
                I[0] = 1;     I[1] = 0;     I[2] = 0;
                I[4] = 0;     I[5] = c;     I[6] = s;
                I[8] = 0;     I[9] = -s;     I[10] = c;
                I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
                I[15] = 1;
            }
        }
        return true;
    }
}
