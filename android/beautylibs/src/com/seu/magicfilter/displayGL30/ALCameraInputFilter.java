package com.seu.magicfilter.displayGL30;

import android.annotation.SuppressLint;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.rednovo.ace.core.video.VideoManager;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGLUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressLint("NewApi")
public class ALCameraInputFilter extends GPUImageFilter {
	private static final String CAMERA_INPUT_VERTEX_SHADER = "" + "attribute vec4 position;\n" + "attribute vec4 inputTextureCoordinate;\n" + "\n" + "uniform mat4 textureTransform;\n" + "varying vec2 textureCoordinate;\n" + "\n" + "void main()\n" + "{\n"
			+ "	textureCoordinate = (textureTransform * inputTextureCoordinate).xy;\n" + "	gl_Position = position;\n" + "}";

	private static final String CAMERA_INPUT_FRAGMENT_SHADER = "" + "#extension GL_OES_EGL_image_external : require\n" + "varying highp vec2 textureCoordinate;\n" + "\n" + "uniform samplerExternalOES inputImageTexture;\n" + "\n" + "void main()\n" + "{\n"
			+ "	gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" + "}";

	private float[] mTextureTransformMatrix;
	private int mTextureTransformMatrixLocation;
	private int mSingleStepOffsetLocation;

	public static int[] mFrameBuffers = null;
	protected static int[] mFrameBufferTextures = null;
	private int mFrameWidth = -1;
	private int mFrameHeight = -1;
	private IntBuffer buffer;

	public ALCameraInputFilter() {
		super(CAMERA_INPUT_VERTEX_SHADER, CAMERA_INPUT_FRAGMENT_SHADER);
	}

	protected void onInit() {
		super.onInit();
		mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
		mTextureTransformMatrixLocation = GLES20.glGetUniformLocation(mGLProgId, "textureTransform");
	}

	public void setTextureTransformMatrix(float[] mtx) {
		mTextureTransformMatrix = mtx;
	}

	@Override
	public int onDrawFrame(int textureId) {
		GLES20.glUseProgram(mGLProgId);
		if (!isInitialized()) {
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

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		return OpenGLUtils.ON_DRAWN;
	}

	@Override
	public int onDrawFrame(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
		GLES20.glUseProgram(mGLProgId);
		if (!isInitialized()) {
			return OpenGLUtils.NOT_INIT;
		}
		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
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

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		return OpenGLUtils.ON_DRAWN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter#onOutputSizeChanged(int, int)
	 */
	@Override
	public void onOutputSizeChanged(int width, int height) {
		super.onOutputSizeChanged(width, height);
		buffer = IntBuffer.allocate(width * height);
	}

	public int onDrawToTexture(VideoManager mVideoManager, final int textureId) {

		if (mFrameBuffers == null)
			return OpenGLUtils.NO_TEXTURE;

		GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
		GLES20.glUseProgram(mGLProgId);
		if (!isInitialized()) {
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

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		if (mVideoManager != null) {
			GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
			mVideoManager.encodegl(buffer.array(), 0);
		}
		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

		return mFrameBufferTextures[0];
	}

	public int onDrawToTexture(VideoManager mVideoManager, final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {

		if (mFrameBuffers == null)
			return OpenGLUtils.NO_TEXTURE;

		GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
		GLES20.glUseProgram(mGLProgId);
		if (!isInitialized()) {
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

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		if (mVideoManager != null) {
			GLES20.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
			mVideoManager.encodegl(buffer.array(), 0);
		}
		GLES20.glDisableVertexAttribArray(mGLAttribPosition);
		GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

		return mFrameBufferTextures[0];
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
