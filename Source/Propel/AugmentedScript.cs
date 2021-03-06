using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class AugmentedScript : MonoBehaviour
{
	
	private Vector2 targetCoordinates;
	private Vector2 deviceCoordinates;
	private float distanceFromTarget = 0.00004f;
	private float proximity = 0.001f;
	private float sLatitude, sLongitude;
	public float dLatitude = -6.88468f, dLongitude = 107.605945f;
	private bool enableByRequest = true;
	public int maxWait = 10;
	public bool ready = false;
	public Text text;
	private GameObject distanceTextObject;
	private double distance;

	private bool setOriginalValues = true;

	private Vector3 targetPosition;
	private Vector3 originalPosition;

	private float speed = .1f;

	public GameObject FlyingDisk;
	public GameObject Ball;


	//calculates distance between two sets of coordinates, taking into account the curvature of the earth.

	void Start(){
		targetCoordinates = new Vector2 (dLatitude, dLongitude);
		StartCoroutine (getLocation ());
		targetPosition = transform.position;
		originalPosition = transform.position;
	}

	IEnumerator getLocation(){
		LocationService service = Input.location;
		if (!enableByRequest && !service.isEnabledByUser) {
			Debug.Log("Location Services not enabled by user");
			yield break;
		}
		service.Start();
		while (service.status == LocationServiceStatus.Initializing && maxWait > 0) {
			yield return new WaitForSeconds(1);
			maxWait--;
		}
		if (maxWait < 1){
			Debug.Log("Timed out");
			yield break;
		}
		if (service.status == LocationServiceStatus.Failed) {
			Debug.Log("Unable to determine device location");
			yield break;
		} else {
			text.text = "Target Location : "+dLatitude + ", "+dLongitude+"\nMy Location: " + service.lastData.latitude + ", " + service.lastData.longitude;
			sLatitude = service.lastData.latitude;
			sLongitude = service.lastData.longitude;
		}
		//service.Stop();
		ready = true;
		startCalculate ();
	}


	void Update(){
		transform.position = Vector3.Lerp(transform.position, targetPosition, speed);
		//rotate by 1 degree about the y axis every frame
		transform.eulerAngles += new Vector3 (0, 1f, 0);
	}


	public void startCalculate(){
		deviceCoordinates = new Vector2 (sLatitude, sLongitude);
		proximity = Vector2.Distance (targetCoordinates, deviceCoordinates);
		if (proximity <= distanceFromTarget) {
			text.text = text.text + "\nDistance : " + proximity.ToString ();
			text.text += "\nTarget Detected";
			FlyingDisk.SetActive (true);
		} else {
			text.text = text.text + "\nDistance : " + proximity.ToString ();
			text.text += "\nTarget not detected, too far!";
			FlyingDisk.SetActive (true);

		}
	}
}







