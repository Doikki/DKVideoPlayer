package xyz.doikki.videoplayer.render;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.IntRange;

import xyz.doikki.videoplayer.util.L;

public class RenderLayoutMeasure {

    /**
     * 视频宽
     */
    private int mVideoWidth;

    /**
     * 视频高
     */
    private int mVideoHeight;

    /**
     * 视频旋转角度
     * （外层Render在接收到视频带有旋转信息时已经调用了{@link View#setRotation(float)}），所以当时的View已经旋转了
     */
    private int mVideoRotationDegree;

    /**
     * 缩放类型
     */
    private int mAspectRatioType;

    /**
     * 测量所得的宽
     */
    private int mMeasuredWidth;

    /**
     * 测量所得的高
     */
    private int mMeasuredHeight;

    /**
     * 获取宽的测量结果
     *
     * @return
     */
    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    /**
     * 获取高的测量结果
     *
     * @return
     */
    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    /**
     * 设置视频大小
     *
     * @param width  视频内容宽
     * @param height 视频内容高
     */
    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    /**
     * 设置视频旋转角度
     *
     * @param videoRotationDegree 旋转角度
     */
    public void setVideoRotationDegree(@IntRange(from = 0, to = 360) int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * 设置界面缩放类型
     *
     * @param aspectRatioType
     */
    public void setAspectRatioType(@AspectRatioType int aspectRatioType) {
        mAspectRatioType = aspectRatioType;
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     *
     * @param rotationDegree 旋转角度，{@link View#getRotation()}
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec, @IntRange(from = 0, to = 360) int rotationDegree) {
        setVideoRotationDegree(rotationDegree);
        doMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量参数是否已旋转
        boolean isMeasureRotated = mVideoRotationDegree == 90 || mVideoRotationDegree == 270;
        //相对的宽 MeasureSpec
        int relateWidthMeasureSpec;
        //相对的高 MeasureSpec
        int relateHeightMeasureSpec;
        if (isMeasureRotated) {//交换宽高测量参数
            relateWidthMeasureSpec = heightMeasureSpec;
            relateHeightMeasureSpec = widthMeasureSpec;
        } else {
            relateWidthMeasureSpec = widthMeasureSpec;
            relateHeightMeasureSpec = heightMeasureSpec;
        }

        if (mVideoHeight == 0 || mVideoWidth == 0) {//当前视频没有宽高返回看，则直接返回测量结果
            mMeasuredWidth = 1;// View.MeasureSpec.getSize(relateWidthMeasureSpec);
            mMeasuredHeight = 1;//View.MeasureSpec.getSize(relateHeightMeasureSpec);
            return;
        }

        int aspectType = mAspectRatioType;
        if (aspectType == AspectRatioType.MATCH_PARENT) {
            //匹配Parent的大小，则直接返回测量参数
            mMeasuredWidth = relateWidthMeasureSpec;
            mMeasuredHeight = relateHeightMeasureSpec;
            StringBuilder sb = new StringBuilder();
            sb.append("aspectType=").append(aspectType).append("\n");
            sb.append("mMeasuredWidth=").append(mMeasuredWidth).append("\n");
            sb.append("mMeasuredHeight=").append(mMeasuredHeight).append("\n");
            L.i(sb.toString());
            return;
        }

        int videoWidth = mVideoWidth;
        int videoHeight = mVideoHeight;
        int widthSpecMode = View.MeasureSpec.getMode(relateWidthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(relateWidthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(relateHeightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(relateHeightMeasureSpec);
        //视屏图像比例
        float displayAspectRatio = calculateDisplayAspectRatio(aspectType, isMeasureRotated, videoWidth, videoHeight);
        //控件大小比例
        float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
        //是否是图像比例更宽：即视频图像的宽高比 大于了 控件的宽高比，也就是是说控件区域更窄，类似竖屏的情况下显示横屏视频
        boolean isWiderDisplay = displayAspectRatio > specAspectRatio;
        int preMeasuredWidth;
        int preMeasuredHeight;

        if (isWiderDisplay) {//通过控件能够分配的比例跟图像比例做了比较，因此不再需要判断测量Mode
            if (aspectType == AspectRatioType.CENTER_CROP) {//特殊处理，限定高来计算宽
                preMeasuredHeight = heightSpecSize;
                preMeasuredWidth = (int) (preMeasuredHeight * displayAspectRatio);
            } else if (aspectType == AspectRatioType.SCALE_ORIGINAL) {
                preMeasuredWidth = Math.min(widthSpecSize, videoWidth);
                preMeasuredHeight = (int) (preMeasuredWidth / displayAspectRatio);
            } else {//缩放模式
                preMeasuredWidth = widthSpecSize;
                preMeasuredHeight = (int) (preMeasuredWidth / displayAspectRatio);
            }
        } else {
            if (aspectType == AspectRatioType.CENTER_CROP) {
                preMeasuredWidth = widthSpecSize;
                preMeasuredHeight = (int) (preMeasuredWidth / displayAspectRatio);
            } else if (aspectType == AspectRatioType.SCALE_ORIGINAL) {
                preMeasuredHeight = Math.min(heightSpecSize, videoHeight);
                preMeasuredWidth = (int) (preMeasuredHeight * displayAspectRatio);
            } else {//缩放模式
                preMeasuredHeight = heightSpecSize;
                preMeasuredWidth = (int) (preMeasuredHeight * displayAspectRatio);
            }
        }
        mMeasuredWidth = preMeasuredWidth;
        mMeasuredHeight = preMeasuredHeight;

        StringBuilder sb = new StringBuilder();
        sb.append("aspectType=").append(aspectType).append("\n");
        sb.append("isWiderDisplay=").append(isWiderDisplay).append("\n");
        sb.append("displayAspectRatio=").append(displayAspectRatio).append("\n");
        sb.append("videoWidth=").append(videoWidth).append("\n");
        sb.append("videoHeight=").append(videoHeight).append("\n");
        sb.append("widthSpecSize=").append(widthSpecSize).append("\n");
        sb.append("heightSpecSize=").append(heightSpecSize).append("\n");
        sb.append("mMeasuredWidth=").append(mMeasuredWidth).append("\n");
        sb.append("mMeasuredHeight=").append(mMeasuredHeight).append("\n");
        L.i(sb.toString());

//
//        if (widthSpecMode == View.MeasureSpec.EXACTLY || heightSpecMode == View.MeasureSpec.EXACTLY) {
//            //宽和高至少有一边能够确定大小，则按照普通的比例计算即可
//            //宽度固定的情况下
//            if (isWiderDisplay) {//确定宽，计算高
//                preMeasuredWidth = widthSpecSize;
//                preMeasuredHeight = (int) (preMeasuredWidth / displayAspectRatio);
//            } else {
//                preMeasuredHeight = heightSpecSize;
//                preMeasuredWidth = (int) (preMeasuredHeight * displayAspectRatio);
//            }
////            //宽设置为固定大小或者MatchParent的情况
////            if (heightSpecMode == View.MeasureSpec.EXACTLY) {
////
////                //高度也设置为固定值或者MatchParent
////                int[] size = measureExactly(aspectType, isMeasureRotated, videoWidth, videoHeight, widthSpecSize, heightSpecSize);
////                mMeasuredWidth = size[0];
////                mMeasuredHeight = size[1];
////            } else {
////                //高度不定的情况下，限定宽计算高
////                mMeasuredWidth = widthSpecSize;
////                mMeasuredHeight = mMeasuredWidth * videoHeight / videoWidth;
////                if (heightSpecMode == View.MeasureSpec.AT_MOST && mMeasuredHeight > heightSpecSize) {
////                    mMeasuredHeight = heightSpecSize;
////                }
////            }
//        } else if (widthSpecMode == View.MeasureSpec.AT_MOST) {
//            //宽设置为WrapContent的情况
//            if (heightSpecMode == View.MeasureSpec.EXACTLY) {
//                //限定了高度，通过高度计算宽度
//                mMeasuredHeight = heightSpecSize;
//                mMeasuredWidth = mMeasuredHeight * videoWidth / videoHeight;
//                if (mMeasuredWidth > widthSpecSize) {
//                    mMeasuredWidth = widthSpecSize;
//                }
//            } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
//                //高度也为WrapContent
//                int[] size = measureAtMost(aspectType, isMeasureRotated, videoWidth, videoHeight, widthSpecSize, heightSpecSize);
//                mMeasuredWidth = size[0];
//                mMeasuredHeight = size[1];
//            } else {
//                //宽为WrapContent 高为UNSPECIFIED：理论上不会存在这种情况
//                mMeasuredWidth = Math.min(videoWidth, widthSpecSize);
//                mMeasuredHeight = mMeasuredWidth * videoHeight / videoWidth;
//            }
//        } else {
//            //宽设置为UNSPECIFIED
//            if (heightSpecMode == View.MeasureSpec.EXACTLY) {
//                //限定了高度，通过高度计算宽度
//                mMeasuredHeight = heightSpecSize;
//                mMeasuredWidth = mMeasuredHeight * videoWidth / videoHeight;
//            } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
//                mMeasuredHeight = Math.min(videoHeight, heightSpecSize);
//                mMeasuredWidth = mMeasuredHeight * videoWidth / videoHeight;
//            } else {
//                //宽和高都是UNSPECIFIED
//                mMeasuredWidth = videoWidth;
//                mMeasuredHeight = videoHeight;
//            }
//        }
    }

    /**
     * 测量{@link View.MeasureSpec#EXACTLY}情况，即设置为指定的大小或者MatchParent的情况
     * 该测量方法等同于{@link AspectRatioType#SCALE}测量：将图像等比例缩放，适配(图像的)最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面（上下或者左右）可能会与父级控件留有空隙
     *
     * @param aspectRatioType  缩放模式：该情况只生效{@link AspectRatioType#SCALE}
     * @param isMeasureRotated 测量的控件是否旋转：该情况只生效{@link AspectRatioType#SCALE},因此该参数无效
     * @param videoWidth       视频图像宽度
     * @param videoHeight      视频图像高度
     */
    private int[] measureExactly(
            @AspectRatioType int aspectRatioType, boolean isMeasureRotated,
            int videoWidth, int videoHeight,
            int widthSpecSize,
            int heightSpecSize) {
//        以下测量逻辑，等同于SCALE测量
//        float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
//        float displayAspectRatio = (float) videoWidth / (float) videoHeight;
//        //vw / vh > w / h     vw * h > w * vh
//        //是否是更宽的展示：即需求显示的宽高比 大于了 控件的宽高比，也就是是说控件区域更窄，类似竖屏的情况下显示横屏视频
//        boolean isWiderDisplay = displayAspectRatio > specAspectRatio;
//        if (isWiderDisplay) {
//            width = widthSpecSize;
//            height = (int) (width / displayAspectRatio);
//        } else {
//            height = heightSpecSize;
//            width = (int) (height * displayAspectRatio);
//        }
        return measureAtMost(AspectRatioType.SCALE, isMeasureRotated, videoWidth, videoHeight, widthSpecSize, heightSpecSize);
    }

    /**
     * 是否是缩放模式
     *
     * @param aspectRatioType
     * @return
     */
    private boolean isScaleType(@AspectRatioType int aspectRatioType) {
        return aspectRatioType == AspectRatioType.SCALE
                || aspectRatioType == AspectRatioType.SCALE_4_3
                || aspectRatioType == AspectRatioType.SCALE_16_9
                || aspectRatioType == AspectRatioType.SCALE_18_9;
    }

    /**
     * 计算图像的缩放比例
     *
     * @param aspectRatioType  缩放模式
     * @param isMeasureRotated 被测量的控件是否旋转：旋转了则比例要取倒数进行计算
     * @param videoWidth       视频图像宽度
     * @param videoHeight      视频图像高度
     * @return 用于展示的图像比例
     */
    private float calculateDisplayAspectRatio(@AspectRatioType int aspectRatioType, boolean isMeasureRotated, int videoWidth, int videoHeight) {
        float displayAspectRatio;
        switch (aspectRatioType) {
            case AspectRatioType.SCALE_4_3:
                if (isMeasureRotated) {
                    displayAspectRatio = 3.0f / 4.0f;
                } else {
                    displayAspectRatio = 4.0f / 3.0f;
                }
                break;
            case AspectRatioType.SCALE_16_9:
                if (isMeasureRotated) {
                    displayAspectRatio = 9.0f / 16.0f;
                } else {
                    displayAspectRatio = 16.0f / 9.0f;
                }
                break;
            case AspectRatioType.SCALE_18_9:
                if (isMeasureRotated) {
                    displayAspectRatio = 9.0f / 18.0f;
                } else {
                    displayAspectRatio = 18.0f / 9.0f;
                }
                break;
            case AspectRatioType.SCALE:
            case AspectRatioType.SCALE_ORIGINAL:
            case AspectRatioType.CENTER_CROP:
            default:
                //以上模式按照图像比例进行处理
                displayAspectRatio = (float) videoWidth / (float) videoHeight;
                break;
        }
        return displayAspectRatio;
    }

    /**
     * 测量{@link View.MeasureSpec#AT_MOST}情况，即设置为wrap_content的情况
     *
     * @param aspectRatioType  缩放模式
     * @param isMeasureRotated 测量的控件是否旋转：旋转了则比例要取倒数进行计算
     * @param videoWidth       视频图像宽度
     * @param videoHeight      视频图像高度
     */
    @SuppressLint("SwitchIntDef")
    private int[] measureAtMost(@AspectRatioType int aspectRatioType,
                                boolean isMeasureRotated,
                                int videoWidth, int videoHeight,
                                int widthSpecSize,
                                int heightSpecSize) {
        float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
        //视频
        float displayAspectRatio;
        switch (aspectRatioType) {
            case AspectRatioType.SCALE_4_3:
                if (isMeasureRotated) {
                    displayAspectRatio = 3.0f / 4.0f;
                } else {
                    displayAspectRatio = 4.0f / 3.0f;
                }
                break;
            case AspectRatioType.SCALE_16_9:
                if (isMeasureRotated) {
                    displayAspectRatio = 9.0f / 16.0f;
                } else {
                    displayAspectRatio = 16.0f / 9.0f;
                }
                break;
            case AspectRatioType.SCALE_18_9:
                if (isMeasureRotated) {
                    displayAspectRatio = 9.0f / 18.0f;
                } else {
                    displayAspectRatio = 18.0f / 9.0f;
                }
                break;
            case AspectRatioType.SCALE:
            case AspectRatioType.SCALE_ORIGINAL:
            case AspectRatioType.CENTER_CROP:
            default:
                //以上模式按照图像比例进行处理
                displayAspectRatio = (float) videoWidth / (float) videoHeight;
                break;
        }
        //是否是更宽的展示：即需求显示的宽高比 大于了 控件的宽高比，也就是是说控件区域更窄，类似竖屏的情况下显示横屏视频
        boolean isWiderDisplay = displayAspectRatio > specAspectRatio;

        int width;
        int height;
        switch (aspectRatioType) {
            case AspectRatioType.CENTER_CROP:
                //显示区域要大于控件宽度
                if (isWiderDisplay) {
                    // 显示区域不够高，限制高
                    height = heightSpecSize;
                    width = (int) (height * displayAspectRatio);
                } else {
                    //显示区域不够宽，限制宽
                    width = widthSpecSize;
                    height = (int) (width / displayAspectRatio);
                }
                break;
            case AspectRatioType.SCALE_ORIGINAL:
                if (isWiderDisplay) {
                    width = Math.min(videoWidth, widthSpecSize);
                    height = (int) (width / displayAspectRatio);
                } else {
                    height = Math.min(videoHeight, heightSpecSize);
                    width = (int) (height * displayAspectRatio);
                }
                break;
            case AspectRatioType.SCALE:
            case AspectRatioType.SCALE_4_3:
            case AspectRatioType.SCALE_16_9:
            case AspectRatioType.SCALE_18_9:
            default:
                if (isWiderDisplay) {
                    //显示区域太宽，限制宽度
                    width = widthSpecSize;
                    height = (int) (width / displayAspectRatio);
                } else {
                    //显示区域更高，限制高度
                    height = heightSpecSize;
                    width = (int) (height * displayAspectRatio);
                }
                break;
        }
        return new int[]{width, height};
    }

    /**
     * 计算显示区域大小
     *
     * @param aspectRatioType
     * @param preMeasuredWidth 预测量的宽度（不是最终测量结果，还需结合aspectRatioType做最终测量）
     * @param preMeasureHeight 预测量的高度（不是最终测量的高度，还需结合aspectRatioType做最终测量）
     */
    private int[] calculateDisplaySize(@AspectRatioType int aspectRatioType,
                                       boolean isWiderDisplay,
                                       float displayAspectRatio,
                                       int videoWidth,
                                       int videoHeight,
                                       int preMeasuredWidth,
                                       int preMeasureHeight) {
        int width;
        int height;
        switch (aspectRatioType) {
            case AspectRatioType.CENTER_CROP:
                //显示区域要大于控件宽度
                if (isWiderDisplay) {
                    // 显示区域不够高，限制高
                    height = preMeasureHeight;
                    width = (int) (height * displayAspectRatio);
                } else {
                    //显示区域不够宽，限制宽
                    width = preMeasuredWidth;
                    height = (int) (width / displayAspectRatio);
                }
                break;
            case AspectRatioType.SCALE_ORIGINAL:
                if (isWiderDisplay) {
                    width = Math.min(videoWidth, preMeasuredWidth);
                    height = (int) (width / displayAspectRatio);
                } else {
                    height = Math.min(videoHeight, preMeasureHeight);
                    width = (int) (height * displayAspectRatio);
                }
                break;
            case AspectRatioType.SCALE:
            case AspectRatioType.SCALE_4_3:
            case AspectRatioType.SCALE_16_9:
            case AspectRatioType.SCALE_18_9:
            default:
                if (isWiderDisplay) {
                    //显示区域太宽，限制宽度
                    width = preMeasuredWidth;
                    height = (int) (width / displayAspectRatio);
                } else {
                    //显示区域更高，限制高度
                    height = preMeasureHeight;
                    width = (int) (height * displayAspectRatio);
                }
                break;
        }
        return new int[]{width, height};
    }
}
