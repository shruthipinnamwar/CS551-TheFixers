using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class pacmanscript : MonoBehaviour {

	private Vector3 targetPosition;
	private Vector3 originalPosition;
	private float speed = .1f;
	private float val = 15f;


	public GameObject PacMan;
	// Use this for initialization
	void Start () {
		targetPosition = transform.position;
		originalPosition = transform.position;
	}

	// Update is called once per frame
	void Update () {
		transform.position = Vector3.Lerp(transform.position, targetPosition, speed);
		//rotate by 1 degree about the y axis every frame
		transform.eulerAngles += new Vector3 (0, 3f, 0);
	}
}
