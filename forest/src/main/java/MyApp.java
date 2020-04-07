import javax.inject.Inject;

import io.vertx.core.Vertx;

@ForestApplication
class MyApp {
    public static void main(String[] args) {
        Launcher.launch(MyApp.class);
    }
}


@Handler
class MyHandler {
    @Inject
    private Vertx vertx;

    @Get
    public void index() {

    }

    @Intercept()
    public void verifySignature() {

    }

    @Post
    public @JsonResponse String github(@RequestBody String json) {
        vertx.eventBus().publish("",json);
        return "";
    }
}

@interface JsonRequestBody {}
@interface UrlEncodedRequestBody {}
@interface PathVariable {}
@interface QueryVariable{}
@interface HtmlResponse {}
@interface TemplateRendering{}
@interface DownloadFile{}
