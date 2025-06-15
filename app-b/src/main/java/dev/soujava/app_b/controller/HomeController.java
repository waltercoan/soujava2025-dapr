package dev.soujava.app_b.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;
import io.dapr.client.domain.HttpExtension;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Value("${dapr.statestore.name}")
    private String STORENAME;

    @GetMapping()    
    public ResponseEntity index() {
        return ResponseEntity.ok().body("Hello from App B");
    }
    @PostMapping("/startsync")
    public ResponseEntity startBSync(@RequestBody String message) {
        System.out.println("App B started");
        System.out.println("Message received: " + message);
        return ResponseEntity.ok().body("App B started");
    }

    
    @Topic(pubsubName = "pubsub-dapr", name = "topicodapr")
    @PostMapping(path="/startasync", consumes = MediaType.ALL_VALUE)
    public ResponseEntity startBASync(@RequestBody(required = false) CloudEvent<String> cloudEvent) {
        System.out.println("App B started");
        var idMessage = cloudEvent.getId();
        var message = cloudEvent.getData();
        System.out.println("Message " + idMessage +  " received: " + message);
        return ResponseEntity.ok().body("App B started");
    }

    @PostMapping("/state/{msg}")
    public ResponseEntity postState(@PathVariable String msg) {
        System.out.println("App B started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){

            daprClient.saveState(STORENAME, "CHAVE", msg).block();

            
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
        return ResponseEntity.ok().body("App B started");
    }

    @GetMapping("/state")
    public ResponseEntity getState() {
        System.out.println("App B started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){


            var retorno = daprClient.getState(STORENAME, "CHAVE", String.class);
            var msg = retorno.block().getValue();
            System.out.println("Retorno: " + msg);
            return ResponseEntity.ok().body(msg);

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App B");
        }
        
    }
}
