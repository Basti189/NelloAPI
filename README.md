# NelloAPI
The nello public API enables nello users to use nello functionalities programmatically. This API is designed to help nello users manage their nello ones using third-party apps.

# nello auth
https://nelloauth.docs.apiary.io/#reference

# nello public api
https://nellopublicapi.docs.apiary.io/#reference


```Java
import de.wolfsline.nello.api.NelloAPI;
import de.wolfsline.nello.api.events.NelloActionEvent;
import de.wolfsline.nello.api.interfaces.NelloEvent;
import de.wolfsline.nello.api.location.Location;
import de.wolfsline.nello.api.timewindow.TimeWindow;

private NelloAPI api = new NelloAPI();
private String token = "";
private List<Location> locations;

public void startApplication() {
  api.setDebugOutput(true);
  
  //Call this to get a token and store the token
  token = api.requestTokenClientCredentials("<YOUR CLIENT ID>", "<YOUR CLIENT SECRET>");
  locations = api.getLocations(token);
  if (locations != null) {
    api.register(this);
    api.startServer(<YOUR PORT>);
    //Register all actions
    api.setWebhook(token, locations.get(0), "<YOUR WEBHOOK URL>");
    //Register specific actions
    //api.setWebhook(token, locations.get(0), "<YOUR WEBHOOK URL>", NelloActionEvent.SWIPE, NelloActionEvent.Deny);
    
    //list available time windows
    List<TimeWindow> listTimeWindows = nello.listTimeWindows(token, locations.get(0));
    for (TimeWindow tw : listTimeWindows) {
      nello.deleteTimeWindow(token, locations.get(0), tw.getId()); //or whatever
    }
    
    api.openDoor(token, locations.get(0));
  }
}

public void closeApplication() {
  api.deleteWebhook(token, locations.get(0));
  api.stopServer();
}

@NelloEvent
public void onNelloActionEvent(NelloActionEvent event) {
  System.out.println(event.toString()); //or whatever
  String msg = "";
  switch(event.getActionID()) {
    case NelloActionEvent.SWIPE:
      msg = event.getName() + " hat die Türe geöffnet";
      break;
    case NelloActionEvent.GEO:
      msg = "Unknown geo message";
      break;
    case NelloActionEvent.TW:
      msg = "Die Türe wurde durch \"" + event.getName() + "\" geöffnet";
      break;
    case NelloActionEvent.DENY:
      msg = "Jemand hat geklingelt, aber nello hat nicht geöffnet";
      break;
    }
}
```
