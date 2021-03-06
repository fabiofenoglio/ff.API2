# ff.API2

Porting to Java of the ffAPI python library some of you guys are using to interface to my API.

Please note:
  - Files upload still not supported
  - Not as tested as the py version
  - I will probably remove public commands endpoints soon

### Sample usage

Pretty simple:

create a request provider and configure it
```sh
ApiProvider provider = new ApiProvider("your API key");

provider.configuration.username = "your username";
provider.configuration.password = "your password";
provider.configuration.useSSL = true;
```

to make a request, create one directly from the provider and configure it
```sh
ApiRequest request = provider.createRequest();
request.command = "user.info";
request.addPost("yourAdditionalParam1", "123");
request.addPost("yourAdditionalParam2", "456");
```

then execute it and get the response
```sh
ApiResponse response = request.execute();

if (response.success) {
    System.out.println("Welcome " + (String)response.data.get("name") + " !");
}
else {
    System.out.println("Error: " + ApiUtils.getCodeDescription(response.code));
}
```

i'm adding hooks should you need it:
```sh
provider.registerHook(ApiHookPoints.BEFORE_REQUEST, new ApiHook() {
	@Override
	public void hook(ApiHookDataBundle data) {
		System.out.println("sending api request to " + (String)data.get("url") + " ...");
	}
});
	
provider.registerHook(ApiHookPoints.REQUEST_EXCEPTION, new ApiHook() {
	@Override
	public void hook(ApiHookDataBundle data) {
		System.out.println("error sending api request: " + ((Exception)data.get("exception")).getMessage());
	}
});
```
