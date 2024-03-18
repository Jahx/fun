package my.group;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Response;

import java.util.List;
import java.util.Objects;


public class HttpServerVerticle extends AbstractVerticle {

    private static final String SET_NAME = "requests";

    private RedisAPI redis;

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpServerVerticle())
                .onFailure(Throwable::printStackTrace);

    }

    @Override
    public void start() {

        var host = "redis://:this_is_password@localhost/1";


        var redisOptions = new RedisOptions();
        redisOptions.setConnectionString(host);

        Redis client = io.vertx.redis.client.Redis.createClient(vertx, redisOptions);
        redis = RedisAPI.api(client);

        var router = Router.router(vertx);

        router.post("/create").handler(BodyHandler.create()).handler(this::create);
        router.get("/read/:key").handler(this::read);
        router.put("/update").handler(BodyHandler.create()).handler(this::update);
        router.delete("/delete/:key").handler(this::delete);

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8282);

    }

    private void create(RoutingContext ctx) {
        String key = ctx.body().asJsonObject().getString("key");
        String value = ctx.body().asJsonObject().getString("value");
        redis.hsetnx(SET_NAME, key, value, res -> {
            if (res.succeeded()) {
                ctx.response().end("Created");
            } else {
                ctx.response().end("Failed to create");
            }
        });
    }

    private void read(RoutingContext ctx) {
        String key = ctx.pathParam("key");
        redis.hget(SET_NAME, key, res -> {
            if (res.succeeded()) {
                if (Objects.isNull(res.result())) {
                    ctx.response().setStatusCode(404).end("Key not found");
                    return;
                }
                Response response = res.result();
                ctx.response().end(response.toString());
            } else {
                ctx.response().end("Failed to read");
            }
        });
    }

    private void update(RoutingContext ctx) {
        String key = ctx.body().asJsonObject().getString("key");
        String value = ctx.body().asJsonObject().getString("value");
        redis.hset(List.of(SET_NAME, key, value), res -> {
            if (res.succeeded()) {
                ctx.response().end("Updated");
            } else {
                ctx.response().end("Failed to update");
            }
        });
    }

    private void delete(RoutingContext ctx) {
        String key = ctx.pathParam("key");
        redis.hdel(List.of(SET_NAME, key), res -> {
            if (res.succeeded()) {
                ctx.response().end("Deleted");
            } else {
                ctx.response().end("Failed to delete");
            }
        });
    }

}