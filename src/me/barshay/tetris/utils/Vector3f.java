package me.barshay.tetris.utils;

public class Vector3f {
	public float x,y,z;
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	public Vector3f plusVector3f(Vector3f shift) {
		this.x += shift.x;
		this.y += shift.y;
		this.z += shift.z;
		return this;
	}
}
