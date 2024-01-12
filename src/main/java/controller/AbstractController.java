package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public abstract class AbstractController implements Controller{
	abstract void doPost(HttpRequest request, HttpResponse response);
	abstract void doGet(HttpRequest request, HttpResponse response);
}
