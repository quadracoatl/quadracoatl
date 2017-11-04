
package org.quadracoatl.framework.common;

import org.quadracoatl.framework.common.vectors.Vector3d;

public class Camera {
	protected static final double DEFAULT_ASPECT_RATIO = 16.0d / 9.0d;
	protected static final double DEFAULT_FOV = 90.0d;
	protected static final double DEFAULT_VIEW_DISTANCE = 150.0d;
	// TODO This is needed for debugging, but should not be public.
	public Vector3d[] pyramidBasePoints = new Vector3d[] {
			new Vector3d(),
			new Vector3d(),
			new Vector3d(),
			new Vector3d() };
	// TODO This is needed for debugging, but should not be public.
	public Vector3d[] pyramidNormals = new Vector3d[] {
			new Vector3d(),
			new Vector3d(),
			new Vector3d(),
			new Vector3d(),
			new Vector3d() };
	private double aspectRatio = DEFAULT_ASPECT_RATIO;
	private Vector3d direction = new Vector3d(1.0d, 0.0d, 0.0d);
	private double horizontalFov = DEFAULT_FOV;
	private Vector3d location = new Vector3d(0.0d, 0.0d, 0.0d);
	private double viewDistance = DEFAULT_VIEW_DISTANCE;
	private double viewDistanceSquared = 0.0d;
	
	public Camera() {
		super();
		
		setFov(DEFAULT_FOV);
		setViewDistance(DEFAULT_VIEW_DISTANCE);
	}
	
	public void changeViewDistance(double change) {
		viewDistance = viewDistance + change;
		viewDistanceSquared = viewDistance * viewDistance;
	}
	
	public double getAspectRatio() {
		return aspectRatio;
	}
	
	public Vector3d getDirection() {
		return direction;
	}
	
	public double getFov() {
		return horizontalFov;
	}
	
	public Vector3d getLocation() {
		return location;
	}
	
	public double getViewDistance() {
		return viewDistance;
	}
	
	public boolean isInsideViewCube(double x, double y, double z) {
		return Math.abs(location.x - x) < viewDistance
				&& Math.abs(location.y - y) < viewDistance
				&& Math.abs(location.z - z) < viewDistance;
	}
	
	public boolean isInsideViewPyramid(double x, double y, double z) {
		return isInsideViewSphere(x, y, z)
				&& isInsidePyramidSide(0, x, y, z)
				&& isInsidePyramidSide(1, x, y, z)
				&& isInsidePyramidSide(2, x, y, z)
				&& isInsidePyramidSide(3, x, y, z)
				&& isInsidePyramidSide(4, x, y, z);
	}
	
	public boolean isInsideViewSphere(double x, double y, double z) {
		double relativeX = x - location.x;
		double relativeY = y - location.y;
		double relativeZ = z - location.z;
		
		return (relativeX * relativeX + relativeY * relativeY + relativeZ * relativeZ) <= viewDistanceSquared;
	}
	
	public void move(double changeX, double changeY, double changeZ) {
		setLocation(
				location.x + changeX,
				location.y + changeY,
				location.z + changeZ);
	}
	
	public void rotate(double degreeX, double degreeY, double degreeZ) {
		direction.rotate(
				Math.toRadians(degreeX),
				Math.toRadians(degreeY),
				Math.toRadians(degreeZ));
		
		updateViewPyramid();
	}
	
	public void setDirection(double x, double y, double z) {
		direction.x = x;
		direction.y = y;
		direction.z = z;
		
		direction.normalize();
		
		updateViewPyramid();
	}
	
	public void setDirection(Vector3d direction) {
		setDirection(direction.x, direction.y, direction.z);
	}
	
	public void setFov(double horizontalFov) {
		this.horizontalFov = horizontalFov;
		
		updateViewPyramid();
	}
	
	public void setLocation(double x, double y, double z) {
		location.x = x;
		location.y = y;
		location.z = z;
		
		updateViewPyramid();
	}
	
	public void setLocation(Vector3d location) {
		setLocation(location.x, location.y, location.z);
	}
	
	public void setViewDistance(double viewDistance) {
		this.viewDistance = viewDistance;
		viewDistanceSquared = viewDistance * viewDistance;
	}
	
	public void zoom(double change) {
		setFov(horizontalFov + change);
	}
	
	private boolean isInsidePyramidSide(int sideIndex, double x, double y, double z) {
		Vector3d normal = pyramidNormals[sideIndex];
		
		return ((normal.x * x) + (normal.y * y) + (normal.z * z)) > 0.0d;
	}
	
	private void updatePyramidBasePoint(int index, double radiansY, double radiansZ) {
		Vector3d basePoint = pyramidBasePoints[index];
		
		basePoint.set(direction);
		basePoint.rotate(0.0d, radiansY, radiansZ);
		basePoint.multiply(viewDistance);
		basePoint.add(location);
	}
	
	private void updateViewPyramid() {
		double fovHalf = horizontalFov / 2.0d;
		double fovHalfInRadians = Math.toRadians(fovHalf);
		double fovHalfInRadiansAspect = fovHalfInRadians / aspectRatio;
		
		updatePyramidBasePoint(0, -fovHalfInRadians, -fovHalfInRadiansAspect);
		updatePyramidBasePoint(1, -fovHalfInRadians, fovHalfInRadiansAspect);
		updatePyramidBasePoint(2, fovHalfInRadians, fovHalfInRadiansAspect);
		updatePyramidBasePoint(3, fovHalfInRadians, -fovHalfInRadiansAspect);
		
		Vector3d.normal(location, pyramidBasePoints[1], pyramidBasePoints[0], pyramidNormals[0]);
		Vector3d.normal(location, pyramidBasePoints[2], pyramidBasePoints[1], pyramidNormals[1]);
		Vector3d.normal(location, pyramidBasePoints[3], pyramidBasePoints[2], pyramidNormals[2]);
		Vector3d.normal(location, pyramidBasePoints[0], pyramidBasePoints[3], pyramidNormals[3]);
		Vector3d.normal(pyramidBasePoints[1], pyramidBasePoints[0], pyramidBasePoints[2], pyramidNormals[4]);
	}
}
