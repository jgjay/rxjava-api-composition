@Grab("io.ratpack:ratpack-groovy:0.9.0")
import static ratpack.groovy.Groovy.*
 
@Grab("io.ratpack:ratpack-jackson:0.9.0")
import ratpack.jackson.JacksonModule
import static ratpack.jackson.Jackson.json
 
@Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.6")
import groovyx.net.http.*
 
@Grab("com.netflix.rxjava:rxjava-groovy:0.16.1")
import rx.Observable
 
@Grab("com.ning:async-http-client:1.7.22")
import com.ning.http.client.*
import java.util.concurrent.Future

@Grab("joda-time:joda-time:2.3")
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
 
import groovy.json.JsonSlurper
 
httpClient = new AsyncHttpClient()
 
ratpack {
  modules { register new JacksonModule() }
  handlers {
    get("ping") { render "Ping OK" }
    get("storylines") {
      def storylines = getStorylines().take(5)
        .map     { s -> [ id:s.'@id', title:s.preferredLabel, summary:s.disambiguationHint, total:s.'metric:tagUsageCount' ] }
        .flatMap { s -> 
          getCreativeWorks(s.id).take(5)
            .map { c -> [title:c.title, summary:c.description] }.toList()
            .map { l -> s + [content:l] }
        }
 
      storylines.toList().subscribe(
        { render json(it.sort { s -> s.total }.reverse()) },
        { e -> render e }
      )
    }
  }
}
 
// Everything below this line would be part of a common set of client libraries built on top of RxJava, abstracting
// away the details of the implementation (synchronous vs asynchronous, caching logic, etc.)
 
def getCreativeWorks(about) {
  today = ISODateTimeFormat.dateTime().print(DateTime.now().withTime(0, 0, 0, 0))
  observable = Observable.create { observer ->
    try {
      httpClient.prepareGet("https://api.live.bbc.co.uk/ldp-core/creative-works")
        .addQueryParameter("type", "cwork:CreativeWork")
        .addQueryParameter("since", today)
        .addQueryParameter("about", about)
        .setHeader("Accept", "application/json-ld")
        .execute(observableCompletionHandler(observer))
    } catch (Throwable t) {
      observer.onError(t)
    }
    rx.subscriptions.Subscriptions.create({})
  }
 
  observable
    .map     { response -> new JsonSlurper().parseText(response.getResponseBody()) }
    .flatMap { response -> Observable.from(response.results) }
}
 
def getStorylines() {
  today = ISODateTimeFormat.dateTime().print(DateTime.now().withTime(0, 0, 0, 0))
  observable = Observable.create { observer ->
    try {
      httpClient.prepareGet("https://api.live.bbc.co.uk/ldp-core/tag-concepts-usage")
        .addQueryParameter("since", today)
        .setHeader("Accept", "application/json-ld")
        .execute(observableCompletionHandler(observer))
    } catch (Throwable t) {
      observer.onError(t)
    }
    rx.subscriptions.Subscriptions.create({})
  }
 
  observable
    .map     { response -> new JsonSlurper().parseText(response.getResponseBody()) }
    .flatMap { response -> Observable.from(response.results) }
}
 
def observableCompletionHandler(observer) {
  new AsyncCompletionHandler<Response>() {
    def Response onCompleted(Response response) { 
      if (response.getStatusCode() != 200) { observer.onCompleted() }
      else { observer.onNext(response); observer.onCompleted(); response }
    }
    
    def void onThrowable(Throwable t) {
      println "An error occurred -> $t"
      observer.onError(t)
    }
  }
}
