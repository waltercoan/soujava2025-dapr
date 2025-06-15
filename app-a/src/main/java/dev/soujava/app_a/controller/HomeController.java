package dev.soujava.app_a.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api")
public class HomeController {

    @Value("${dapr.service.app-b.name}")
    private String SERVICE_APP_B;
    @Value("${dapr.pubsub.name}")
    private String PUBSUBNAME;
    @Value("${dapr.topic.name}")
    private String TOPICNAME;
    @Value("${dapr.statestore.name}")
    private String STORENAME;
    @Value("${dapr.configurationstore.name}")
    private String DAPR_CONFIGURATON_STORE;
    @Value("${dapr.secretstore.name}")
    private String SECRETSTORENAME;

    @GetMapping()
    public ResponseEntity index() {
        return ResponseEntity.ok().body("Hello from App A");
    }
    
    @PostMapping("/startsync")
    public ResponseEntity startASync() {
        System.out.println("App A started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){
            var message = "Hello from App A";
            daprClient.invokeMethod(SERVICE_APP_B, "/api/startsync", message, HttpExtension.POST).block();

            
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
        return ResponseEntity.ok().body("App A started");
    }
    @PostMapping("/startasync")
    public ResponseEntity startAASync() {
        System.out.println("App A started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){
            var message = "Hello from App A";
            daprClient.publishEvent(PUBSUBNAME, TOPICNAME, message).block();

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
        return ResponseEntity.ok().body("App A started");
    }


    @PostMapping("/state/{msg}")
    public ResponseEntity postState(@PathVariable String msg) {
        System.out.println("App A started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){

            daprClient.saveState(STORENAME, "CHAVE", msg).block();

            
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
        return ResponseEntity.ok().body("App A started");
    }

    @GetMapping("/state")
    public ResponseEntity getState() {
        System.out.println("App A started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){


            var retorno = daprClient.getState(STORENAME, "CHAVE", String.class);
            var msg = retorno.block().getValue();
            System.out.println("Retorno: " + msg);
            return ResponseEntity.ok().body(msg);

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
    }

    @GetMapping("/config")
    public ResponseEntity getConfig() {
        System.out.println("App A started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){

            var retorno = daprClient.getConfiguration(DAPR_CONFIGURATON_STORE, "URL_DATABASE");
            var msg = retorno.block().getValue();
            System.out.println("Retorno: " + msg);
            return ResponseEntity.ok().body(msg);

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
    }

    @GetMapping("/secret")
    public ResponseEntity getSecret() {
        System.out.println("App A started");
        try(DaprClient daprClient = new DaprClientBuilder().build()){

            var segredo = daprClient.getSecret(SECRETSTORENAME,"MEUSEGREDO").block();
            
            System.out.println("Retorno: " + segredo);
            return ResponseEntity.ok().body(segredo);

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error starting App A");
        }
    }
   
}