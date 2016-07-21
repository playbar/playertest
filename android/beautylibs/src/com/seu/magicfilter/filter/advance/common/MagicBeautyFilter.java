package com.seu.magicfilter.filter.advance.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.rednovo.ace.core.video.NovoAVEngine;
import com.rednovo.ace.core.video.VideoManager;
import com.rednovo.btlibs.R;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterParam;
import com.seu.magicfilter.utils.OpenGLUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressLint("NewApi")
public class MagicBeautyFilter extends GPUImageFilter {

	private int mSingleStepOffsetLocation;
	private int mParamsLocation;

	private float[] mTextureTransformMatrix;
	private int mTextureTransformMatrixLocation;

	public static int[] mFrameBuffers = null;
	protected static int[] mFrameBufferTextures = null;

	private IntBuffer ib;

	private int mFrameWidth = -1;
	private int mFrameHeight = -1;

	public MagicBeautyFilter(Context context) {

		super(OpenGLUtils.readShaderFromRawResource(context, R.raw.beautify_vertex_camera), (MagicFilterParam.mGPUPower == 1 ? OpenGLUtils.readShaderFromRawResource(context, R.raw.beautify_fragment) : OpenGLUtils.readShaderFromRawResource(context, R.raw.beautify_fragment)));
	}

	public void setTextureTransformMatrix(float[] mtx) {
		mTextureTransformMatrix = mtx;
	}

	protected void onInit() {
		super.onInit();
		mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
		mParamsLocation = GLES20.glGetUniformLocation(getProgram(), "params");
		mTextureTransformMatrixLocation = GLES20.glGetUniformLocation(getProgram(), "textureTransform");
		setBeautyLevel(3);
	}

	public int onDrawFrameBeauty(VideoManager en, final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {

		if (mFrameBuffers == null)
			return OpenGLUtils.NO_TEXTURE;

		GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

		GLES20.glUseProgram(mGLProgId);
		runPendingOnDrawTasks();
		if (!mIsInitialized) {
			return OpenGLUtils.NOT_INIT;
		}

		cubeBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribPosition);
		textureBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0);

		if (textureId != OpenGLUtils.NO_TEXTURE) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
			GLES20.glUniform1i(mGLUniformTexture, 0);
		}

		onDrawArraysPre();
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		if (en != null) {
			GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
			en.encodegl(ib.array(), 0);
		}

		// if (st != null) {
		// GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		// Bitmap bitmap = Bitmap.createBitmap(mOutputWidth, mOutputHeight, Bitmap.Config.ARGB_8888);
		// bitmap.copyPixelsFromBuffer(IntBuffer.wrap(ib.array()));
		// st.execute(bitmap);
		// }

		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		onDrawArraysAfter();
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

		return mFrameBufferTextures[0];
	}

	public int onDrawFrameBeauty(NovoAVEngine en, final int textureId) {

		if (mFrameBuffers == null)
			return OpenGLUtils.NO_TEXTURE;

		GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

		GLES20.glUseProgram(mGLProgId);
		runPendingOnDrawTasks();
		if (!mIsInitialized) {
			return OpenGLUtils.NOT_INIT;
		}

		mGLCubeBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribPosition);
		mGLTextureBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
		GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0);
		if (textureId != OpenGLUtils.NO_TEXTURE) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
			GLES20.glUniform1i(mGLUniformTexture, 0);
		}

		onDrawArraysPre();
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		if (en != null) {
			GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
			en.Encodegl(ib.array(), 0);
		}

		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		onDrawArraysAfter();
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

		return mFrameBufferTextures[0];
	}

	protected void onDestroy() {
		super.onDestroy();
		destroyFramebuffers();
	}

	private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] { 2.0f / w, 2.0f / h });
	}

	@Override
	public void onOutputSizeChanged(final int w, final int h) {
		super.onOutputSizeChanged(w, h);
		ib = IntBuffer.allocate(w * h);
		setTexelSize(w, h);
		initCameraFrameBuffer(w, h);
	}

	public void setBeautyLevel(int level) {
		switch (level) {
			case 1:
				setFloatVec4(mParamsLocation, new float[] { 1.0f, 1.0f, 0.15f, 0.15f });
				break;
			case 2:
				setFloatVec4(mParamsLocation, new float[] { 0.8f, 0.9f, 0.2f, 0.2f });
				break;
			case 3:
				setFloatVec4(mParamsLocation, new float[] { 0.6f, 0.8f, 0.25f, 0.25f });
				break;
			case 4:
				setFloatVec4(mParamsLocation, new float[] { 0.4f, 0.7f, 0.38f, 0.3f });
				break;
			case 5:
				setFloatVec4(mParamsLocation, new float[] { 0.33f, 0.63f, 0.4f, 0.35f });
				break;
			default:
				break;
		}
	}

	public void initCameraFrameBuffer(int width, int height) {
		if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height))
			destroyFramebuffers();
		if (mFrameBuffers == null) {
			mFrameWidth = width;
			mFrameHeight = height;
			mFrameBuffers = new int[1];
			mFrameBufferTextures = new int[1];

			GLES20.glGenFramebuffers(1, mFrameBuffers, 0);

			GLES20.glGenTextures(1, mFrameBufferTextures, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		}
	}

	public void destroyFramebuffers() {
		if (mFrameBufferTextures != null) {
			GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
			mFrameBufferTextures = null;
		}
		if (mFrameBuffers != null) {
			GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
			mFrameBuffers = null;
		}
		mFrameWidth = -1;
		mFrameHeight = -1;
	}

}
