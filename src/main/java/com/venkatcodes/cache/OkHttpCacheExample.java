package com.venkatcodes.cache;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.CacheResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OkHttpCacheExample {
    private static final int CACHE_MAX_AGE_SECONDS = 60; // 1 minute

    public static void main(String[] args) {
        try {
            // 1. Create a cache directory
            File cacheDir = new File("/Users/venkats/Documents/GitHub/CacheDir2");
            cacheDir.mkdirs();

            // 2. Create a cache instance
            int cacheSize = 1 * 1024 * 1024 ; // 10 MB cache size
            Cache cache = new Cache(cacheDir, cacheSize);

            // 3. Create an OkHttpClient instance with the cache enabled
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();

            // 4. Create a request with cache control
            Request request = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            //corruptCacheEntry(cacheDir);

            // 5. Execute the request and handle the response
            Response response = client.newCall(request).execute();
            if (response.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response.body().string());
            }

            printResponseDetails(response);
            Request request2 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            Response response2 = client.newCall(request2).execute();
            if (response2.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response2.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response2.body().string());
            }
            //corruptCacheEntry(cacheDir);

            printResponseDetails(response2);
            corruptCacheCertificate(cacheDir);
            Request request3 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            Response response3 = client.newCall(request3).execute();
            if (response3.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response3.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response3.body().string());
            }

            Request request4 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            Response response4 = client.newCall(request4).execute();
            if (response4.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response4.cacheResponse());
                Handshake handshake = response4.handshake();
                if (handshake != null) {
                    List<Certificate> peerCertificates = handshake.peerCertificates();
                    for (Certificate certificate : peerCertificates) {
                        System.out.println("Certificate: " + certificate);
                    }
                } else {
                    System.out.println("No handshake information available for the cached response.");
                }
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response4.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5. Read the cache metadata

    }

    private static void printResponseDetails(Response response) throws IOException {
        System.out.println("Response URL: " + response.request().url());
        System.out.println("Response Code: " + response.code());
        System.out.println("Response Protocol: " + response.protocol());
        System.out.println("Response Message: " + response.message());
        System.out.println("Response Handshake: " + response.handshake());
        System.out.println("Response Cache Response: " + response.cacheResponse());
        System.out.println("Response Network Response: " + response.networkResponse());
        System.out.println("Response Headers: " + response.headers());
        System.out.println("Response Body: " + response.body().string());
        System.out.println("---------------");
    }

    private static void corruptCacheCertificate(File cacheDirectory) throws IOException {
        // Locate the cache files
        Files.walk(cacheDirectory.toPath())
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        // Read the contents of the file
                        byte[] fileContent = Files.readAllBytes(filePath);

                        // Corrupt the part of the file where the certificate is likely stored
                        // For simplicity, we'll just overwrite some bytes in the middle of the file
                        int corruptPosition = fileContent.length / 2;
                        for (int i = 0; i < 10; i++) {
                            fileContent[corruptPosition + i] = (byte) 0xFF;
                        }

                        // Write the corrupted content back to the file
                        Files.write(filePath, fileContent);
                        System.out.println("Corrupted cache file: " + filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void corruptCacheEntry(File cacheDirectory) throws IOException {
        File[] files = cacheDirectory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".0")) {
                    Path path = Paths.get(file.getPath());
                    byte[] data = Files.readAllBytes(path);
                    // Modify a portion of the data to be invalid
                    for (int i = 0; i < 10; i++) {
                        data[i] = 0;
                    }
                    Files.write(path, data);
                    System.out.println("Corrupted cache entry: " + file.getPath());
                }
            }
        }
    }

    /*
/Users/venkats/Library/Java/JavaVirtualMachines/corretto-11.0.20.1/Contents/Home/bin/java -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:57128,suspend=y,server=n -javaagent:/Users/venkats/Library/Caches/JetBrains/IdeaIC2022.2/captureAgent/debugger-agent.jar -Dfile.encoding=UTF-8 -classpath /Users/venkats/Documents/GitHub/OkHttpCache/target/classes:/Users/venkats/.m2/repository/com/squareup/okhttp3/okhttp/4.9.2/okhttp-4.9.2.jar:/Users/venkats/.m2/repository/com/squareup/okio/okio/2.8.0/okio-2.8.0.jar:/Users/venkats/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib-common/1.4.0/kotlin-stdlib-common-1.4.0.jar:/Users/venkats/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib/1.4.10/kotlin-stdlib-1.4.10.jar:/Users/venkats/.m2/repository/org/jetbrains/annotations/13.0/annotations-13.0.jar:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar com.venkatcodes.cache.OkHttpCacheExample
Connected to the target VM, address: '127.0.0.1:57128', transport: 'socket'
Response from cache: Response{protocol=http/1.1, code=200, message=, url=https://www.vogella.com/}
Response URL: https://www.vogella.com/
Response Code: 200
Response Protocol: h2
Response Message:
Response Handshake: Handshake{tlsVersion=TLS_1_3 cipherSuite=TLS_AES_256_GCM_SHA384 peerCertificates=[CN=vogella.com, CN=R3, O=Let's Encrypt, C=US, CN=ISRG Root X1, O=Internet Security Research Group, C=US] localCertificates=[]}
Response Cache Response: Response{protocol=http/1.1, code=200, message=, url=https://www.vogella.com/}
Response Network Response: Response{protocol=h2, code=200, message=, url=https://www.vogella.com/}
Response Headers: last-modified: Mon, 03 Jun 2024 07:17:47 GMT
etag: "4fb6-619f72158796c-gzip"
accept-ranges: bytes
vary: Accept-Encoding
content-type: text/html
date: Mon, 03 Jun 2024 14:28:18 GMT
server: Apache/2.4.52 (Ubuntu)

Response Body:
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Eclipse, Android and Java training and support</title>

	<meta name="description" content="The vogella GmbH is a German company and provides services ranging from training, consulting and mentoring in the areas of Eclipse, Flutter and Git.">
	<meta name="keywords" content="Training, Tutorials, Exercises, Eclipse, RCP, OSGi, Android, GWT, JUnit, XML, JSF, JPA, Git">
	<meta name="verify-v1" content="sE7LNm8dZTyJgkDU7KR/1Hw5klYayq9ow10fOEcUHY0=">
	<meta name="robots" content="index, follow">
	<meta name="author" content="Lars Vogel">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<link href="https://www.vogella.com/css/navigation.css?v=2.9" rel="stylesheet" type="text/css">
	<link href="https://www.vogella.com/css/header.css?v=2.0" rel="stylesheet" type="text/css">
	<link href="https://www.vogella.com/css/companyfooter.css?v=2.0" rel="stylesheet" type="text/css">
	<link href="https://www.vogella.com/css/trainingoffering.css?v=2.2" rel="stylesheet" type="text/css">

	<link rel="shortcut icon" href="https://www.vogella.com/favicon.ico">
	<link rel="alternate" type="application/rss+xml" title="RSS" href="http://feeds.feedburner.com/EclipseAndJava">
	<link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="vogella" />
	<link rel="copyright" href="//creativecommons.org/licenses/by-nc-sa/3.0/de/">

	<!-- Google Analytics -->
	<script>
		window.dataLayer = window.dataLayer || [];
		function gtag(){dataLayer.push(arguments);}

		gtag('consent', 'default', {
			'ad_storage': 'denied',
			'analytics_storage': 'denied',
			'region': ['BE', 'BG', 'CZ', 'DK', 'DE', 'EE', 'IE', 'GR', 'ES', 'FR', 'HR', 'IT', 'CY', 'LV', 'LT', 'LU', 'HU', 'MT', 'NL', 'AT', 'PL', 'PT', 'RO', 'SI', 'SK', 'FI', 'SE', 'US-CA', 'US-CO'],
			'wait_for_update': 500
		});
		gtag('set', 'ads_data_redaction', true);
	</script>

	<script async src="https://www.googletagmanager.com/gtag/js?id=UA-3967758-1"></script>
	<script>
		window.dataLayer = window.dataLayer || [];
		function gtag(){dataLayer.push(arguments);}

		gtag('js', new Date());
		gtag('config', 'UA-3967758-1', {
			'anonymize_ip': true,
			'cookie_domain': 'vogella.com',
			'cookie_flags': 'SameSite=None;Secure'
		});
	</script>
	<!-- End of Google Analytics -->

 	<!-- Quantcast Choice. Consent Manager Tag v2.0 (for TCF 2.0) -->
	<script type="text/javascript" async=true>
	(function() {
	var host = window.location.hostname;
	var element = document.createElement('script');
	var firstScript = document.getElementsByTagName('script')[0];
	var url = 'https://cmp.quantcast.com'
    	.concat('/choice/', 'LU3TErYtTq8Gq', '/', host, '/choice.js?tag_version=V2');
	var uspTries = 0;
	var uspTriesLimit = 3;
	element.async = true;
	element.type = 'text/javascript';
	element.src = url;

	firstScript.parentNode.insertBefore(element, firstScript);

	function makeStub() {
		var TCF_LOCATOR_NAME = '__tcfapiLocator';
		var queue = [];
		var win = window;
		var cmpFrame;

	function addFrame() {
		var doc = win.document;
		var otherCMP = !!(win.frames[TCF_LOCATOR_NAME]);

		if (!otherCMP) {
			if (doc.body) {
				var iframe = doc.createElement('iframe');

				iframe.style.cssText = 'display:none';
				iframe.name = TCF_LOCATOR_NAME;
				doc.body.appendChild(iframe);
			} else {
				setTimeout(addFrame, 5);
			}
		}
		return !otherCMP;
	}

	function tcfAPIHandler() {
		var gdprApplies;
		var args = arguments;

		if (!args.length) {
			return queue;
		} else if (args[0] === 'setGdprApplies') {
			if (
			args.length > 3 &&
			args[2] === 2 &&
			typeof args[3] === 'boolean'
		) {
		gdprApplies = args[3];
		if (typeof args[2] === 'function') {
			args[2]('set', true);
		}
      }
      } else if (args[0] === 'ping') {
			var retr = {
			gdprApplies: gdprApplies,
			cmpLoaded: false,
			cmpStatus: 'stub'
		};

        if (typeof args[2] === 'function') {
          args[2](retr);
        }
      } else {
        if(args[0] === 'init' && typeof args[3] === 'object') {
          args[3] = Object.assign(args[3], { tag_version: 'V2' });
        }
        queue.push(args);
      }
    }

    function postMessageEventHandler(event) {
      var msgIsString = typeof event.data === 'string';
      var json = {};

      try {
        if (msgIsString) {
          json = JSON.parse(event.data);
        } else {
          json = event.data;
        }
      } catch (ignore) {}

      var payload = json.__tcfapiCall;

      if (payload) {
        window.__tcfapi(
          payload.command,
          payload.version,
          function(retValue, success) {
            var returnMsg = {
              __tcfapiReturn: {
                returnValue: retValue,
                success: success,
                callId: payload.callId
              }
            };
            if (msgIsString) {
              returnMsg = JSON.stringify(returnMsg);
            }
            if (event && event.source && event.source.postMessage) {
              event.source.postMessage(returnMsg, '*');
            }
          },
          payload.parameter
        );
      }
    }

    while (win) {
      try {
        if (win.frames[TCF_LOCATOR_NAME]) {
          cmpFrame = win;
          break;
        }
      } catch (ignore) {}

      if (win === window.top) {
        break;
      }
      win = win.parent;
    }
    if (!cmpFrame) {
      addFrame();
      win.__tcfapi = tcfAPIHandler;
      win.addEventListener('message', postMessageEventHandler, false);
    }
  };

  makeStub();

  var uspStubFunction = function() {
    var arg = arguments;
    if (typeof window.__uspapi !== uspStubFunction) {
      setTimeout(function() {
        if (typeof window.__uspapi !== 'undefined') {
          window.__uspapi.apply(window.__uspapi, arg);
        }
      }, 500);
    }
  };

  var checkIfUspIsReady = function() {
    uspTries++;
    if (window.__uspapi === uspStubFunction && uspTries < uspTriesLimit) {
      console.warn('USP is not accessible');
    } else {
      clearInterval(uspInterval);
    }
  };

  if (typeof window.__uspapi === 'undefined') {
    window.__uspapi = uspStubFunction;
    var uspInterval = setInterval(checkIfUspIsReady, 6000);
  }

// Google Analytics integration within Quantcast
	 window.__tcfapi('addEventListener', 2, function(tcData, success) {
       if (success && tcData.gdprApplies && (tcData.eventStatus === 'tcloaded' || tcData.eventStatus === 'useractioncomplete') ) {
         if (tcData.vendor.consents[755] && tcData.purpose.consents[1]) {
  		   gtag('consent', 'update', {
			 'ad_storage': 'granted',
			 'analytics_storage': 'granted'
		   });
		 }
	   }
	 })
	 })();
	</script>
	<!-- End Quantcast Choice. Consent Manager Tag v2.0 (for TCF 2.0) -->

	<!-- Open/close the sidenav -->
	<script>
		function openCloseNav() {
			if (document.getElementById("mobileTopnav").style.width === "150px")
			{
				document.getElementById("mobileTopnav").style.width = "0";
	        	document.body.style.marginLeft = "8px";
	        }
	        else
	        {
				document.getElementById("mobileTopnav").style.width = "150px";
	        	document.body.style.marginLeft = "160px";
	        }
		}
	</script>
	<!-- End of Open/close the sidenav -->

</head>

<body>

<div id="mobileHeaderpart">
    <span onclick="openCloseNav()"><img src="https://www.vogella.com/img/icons/burger.svg" alt="" /> </span>
    <div id="mobileLogo">
        <a title="vogella.com" href="http://www.vogella.com">
            <img src="https://www.vogella.com/img/logo/index_logo.svg" alt="vogella.com"/>
        </a>
    </div>
</div>

<nav id="mobileTopnav" class="sidenav">
    <a  class="currentpage" href="https://www.vogella.com/">Home</a>
	<a  href="https://www.vogella.com/tutorials/">Tutorials</a>
	<a  href="https://www.vogella.com/training/">Training</a>
    <a  href="https://www.vogella.com/consulting/">Consulting</a>
	<a  href="https://www.vogella.com/company/">Company</a>
	<a  style="margin-bottom:60px" href="https://www.vogella.com/contact.html">Contact us</a>
</nav>

<div id="headerpart">
	<div id="logo">
		<a title="vogella.com" href="http://www.vogella.com">
			<img src="https://www.vogella.com/img/logo/index_logo.png" alt="vogella.com"/>
		</a>
	</div>

	<nav id="topnav">
		<ul id="home" class="topnav">
			<li id="home-icon"  class="currentpage" ><a href="https://www.vogella.com/"></a></li>
			<li  > <a href="https://www.vogella.com/tutorials/">Tutorials</a>
				<ul>
					<li><a href="https://www.vogella.com/tutorials/eclipse.html">Eclipse RCP</a></li>
					<li><a href="https://www.vogella.com/tutorials/eclipseide.html">Eclipse IDE</a></li>
					<li><a href="https://www.vogella.com/tutorials/eclipseplatform.html">Eclipse IDE Extensions</a></li>
					<li><a href="https://www.vogella.com/tutorials/flutter.html">Flutter</a></li>
					<li><a href="https://www.vogella.com/tutorials/android.html">Android Programming</a></li>
					<li><a href="https://www.vogella.com/tutorials/web.html">Web</a></li>
					<li><a href="https://www.vogella.com/tutorials/java.html">Java</a></li>
					<li><a href="https://www.vogella.com/tutorials/technology.html">Technology</a></li>
					<li><a href="https://www.vogella.com/tutorials/algorithms.html">Software design</a></li>
					<li><a href="https://github.com/vogellacompany/">Code @ GitHub</a></li>
				</ul>
			</li>

			<li   ><a href="https://www.vogella.com/training/">Training</a>
				<ul>
					<li><a href="https://www.vogella.com/training/eclipse/eclipsercp.html">Eclipse RCP Training</a></li>
					<li><a href="https://www.vogella.com/training/testbuild/javatesting.html">Java Testing Training</a></li>
					<li><a href="https://www.vogella.com/training/appdevelopment/flutter.html">Cross Mobile App Dev. Training</a></li>
					<li><a href="https://www.vogella.com/training/git/index.html">Git Version Control Training</a></li>
					<li><a href="https://learn.vogella.com/">Self-study Portal</a></li>
					<li><a href="https://www.vogella.com/training/onsite/index.html">Other Onsite Training</a></li>
				</ul>
			</li>
			<li  ><a href="https://www.vogella.com/consulting/">Consulting</a></li>

			<li   ><a href="https://www.vogella.com/company/">Company</a>
				<ul>
					<li><a href="https://www.vogella.com/jobs/">Jobs</a></li>
					<li><a href="https://www.vogella.com/people/index.html">People</a></li>
					<li><a href="https://www.vogella.com/customers.html" onclick="_gaq.push(['_trackEvent', 'Customers', 'clicked', 'Header']);">Customers</a></li>
					<li><a href="https://vogella.com/blog/">Blog</a></li>
					<li><a href="https://www.vogella.com/books/">Books</a>
						<ul>
							<li><a href="https://www.vogella.com/books/eclipsercp.html">Eclipse RCP</a></li>
							<li><a href="https://www.vogella.com/books/eclipseide.html">Eclipse IDE</a></li>
							<li><a href="https://www.vogella.com/books/eclipsecontribution.html">Contributing to the Eclipse Project</a></li>
							<li><a href="https://www.vogella.com/books/git.html">Git</a></li>
						</ul>
					</li>
					<li><a href="https://www.vogella.com/research.html">Research Projects</a>
					<li><a href="https://www.paypal.com/donate?hosted_button_id=D2DMTGN3LJGQU">Donate</a></li>
				</ul>
			</li>

			<li   ><a href="https://www.vogella.com/contact.html">Contact us</a></li>
		</ul>
	</nav>

	<div id="searchfixed">
		<form action="https://www.vogella.com/search.html" id="cse-search-box">
			<div id="searchwrapper">
				<input type="hidden" name="cx" value="partner-pub-3851443674951530:3991491935" />
				<input type="hidden" name="cof" value="FORID:10" />
				<input type="hidden" name="ie" value="UTF-8" />
				<input type="text" placeholder="Search" id="search_field" name="q" size="55" />
				<input type="image" src="https://www.vogella.com/img/icons/srch.png" id="search_button" name="sa" alt="Search" />
			</div>
		</form>
	</div>
</div>

<div id="container_new">
<div id="leftcol">

<!-- Empty -->
</div> <!-- leftcolumn -->

<div class="content-wrapper">
	<div class="section_black">
		<div class="half_side">
			<a class="header_dark_background" href="https://www.vogella.com/company/"> vogella</a>
			<div class="description_white">
			We provide <a class="important_links" href="https://learn.vogella.com/">commercial online training</a>,
			<a class="important_links" href="https://www.vogella.com/training/onsite/index.html">onsite training</a>
			and
			<a class="important_links" href="https://www.vogella.com/consulting/">consulting</a>.
			We also publish lots of free
			<a class="important_links" href="https://www.vogella.com/tutorials/">tutorials</a>.
			</div> <!-- description_white -->
		</div> <!-- half_side -->

		<div class="half_side_center">
			<div class="small_img">
				<a href="https://www.vogella.com/company/">
					<img src="./img/logo/logo_very_rough_left_white.png" alt="vogella">
				</a>
			</div> <!--small_img-->
		</div> <!-- half_side -->
	</div> <!-- section_black -->

	<div class="section_gray">

		<div class="half_side_center">
			<div class="big_img">
				<a class="header_light_background" href="https://www.vogella.com/training/">
					<img src="./img/main_page/learningportaldevices.png" alt="LearningPortal">
				</a>
			</div>
		</div> <!-- half_side_center -->
		<div class="half_side">
			<a class="header_light_background" href="https://www.vogella.com/training/"> Training and education</a>
			<div class="description_black">
				Learn directly from our experts
				via our <a class="important_links" href="https://learn.vogella.com/">commercial online training</a>
				or <a class="important_links" href="https://www.vogella.com/training/onsite/index.html">directly from our employees</a>
				in the areas of <a class="important_links" href="https://learn.vogella.com/courses/details/rich-client-platform">Eclipse RCP</a>, Git, Java and mobile development.
			</div>
		</div> <!-- half_side -->

	</div> <!--section_gray-->

	<div class="section_white">
		<div class="half_side_center">
			<a href="https://www.vogella.com/tutorials/">
				<img src="./img/main_page/keywords.png" alt="Tutorials">
			</a>
		</div> <!-- half_side -->

		<div class="half_side">
			<a class="header_light_background" href="https://www.vogella.com/tutorials/"> Tutorials</a>
			<div class="description_black">Read our free online tutorials in the areas of Eclipse, RCP, Git, Java, Dart, Flutter and Web development and others.</div>
		</div> <!-- half_side -->
	</div> <!-- section_white -->


	<div class="section_gray_center">
		<div class="twothird_side_center">
			<a class="header_light_background" href="https://www.vogella.com/customers.html">Customer quotes</a>
			<div id="quotes"> </div>
		</div>
		<div class="third_side_center">
			<img class="customer-quote-img" src="./img/main_page/book_eyes.jpg" alt="Customers Quotes">
		</div>
	</div>

</div> <!-- content wrapper -->
<div id="rightcol">
<!--	<div id="banner2">
		<div id="banner-triangle2"> </div>
		<div id="banner-text2"> NOW <br> Hiring </div>
	</div>-->
</div> <!-- rightcol -->
</div>

<!-- Load jQuery and idTabs -->
<script type="text/javascript" src="https://www.vogella.com/javascript/jquery/jquery.js"></script> <!-- please change the src according to your folder strucutre and where jquery files will be on vogella.de -->
<script type="text/javascript" src="https://www.vogella.com/javascript/jquery/jquery_tabs.js"></script> <!-- please change the src according to your folder strucutre and where jquery files will be on vogella.de -->
<script type="text/javascript">var fade=function(id,s){s.tabs.removeClass(s.selected);s.tab(id).addClass(s.selected);s.items.hide();s.item(id).show();return false;};$.fn.fadeTabs=$.idTabs.extend(fade);$(".menu").fadeTabs();</script>
<!--
<script type="text/javascript">
$(document).ready(function() {
    // change the active tab after the site is loaded
   $('ul li a[href$="agenda"]').removeClass('selected');
   $('#agenda').hide();

   $('ul li a[href$="angebot"]').addClass('selected');
   $('#angebot').show();

});
</script>-->
<script type="text/javascript" src="./training/quotes/vogella/content-quotes.js"></script>
<script type="text/javascript" src="https://www.vogella.com/training/quotes/quotes.js"></script>
<div id="companyfooter">

	<div class="footerrow">
        <h2><a href="https://www.vogella.com/company/">vogella company</a></h2>
        <ul>
            <li><a href="https://www.vogella.com/customers.html" onclick="_gaq.push(['_trackEvent', 'Customers', 'clicked', 'Companyfooter']);">Customers</a></li>
            <li><a href="https://www.vogella.com/faq.html">FAQ</a></li>
            <li><a href="https://www.paypal.com/donate?hosted_button_id=D2DMTGN3LJGQU">Donate for free tutorials</a></li>
            <li><a href="https://www.vogella.com/legal.html" rel="nofollow">Legal</a></li>
            <li><a href="https://www.vogella.com/privacy.html" rel="nofollow">Privacy Policy</a></li>
            <li><a class="change-consent" onclick="window.__tcfapi('displayConsentUi', 2, function() {} );">Change consent</a></li>
        </ul>
    </div><!-- footerrow -->

    <div class="footerrow" id="contact_en">
        <h2><a href="https://www.vogella.com/contact.html?lang=en">Contact us</a></h2>

        <a href="https://vogella.com/contact_en.html" class="darker_link" >
        	<img alt="Email" src="https://www.vogella.com/img/icons/contact_form.svg" title="Email" style="width:20px"/>
        	Contact form
        </a>
        <br>
        <a href="mailto:sales@vogella.com" class="darker_link" >
        	<img alt="Email" src="https://www.vogella.com/img/icons/email_gray.svg" title="Email" style="width:20px"/>
        	sales@vogella.com
        </a>
        <br>
		<a href="tel:+49 40 7880 4360" class="darker_link" >
        <img alt="Call" src="https://www.vogella.com/img/icons/call_gray.svg" title="Call" style="width:20px"/>
        +49 40 7880 4360
		</a>

        <div id="social-icons">
            <a href="http://twitter.com/vogellacompany">
                <img src="https://www.vogella.com/img/common/twitter_small.png" alt="Follow us on twitter" width="32" height="32" pagespeed_url_hash="4214849013" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://www.linkedin.com/company/vogella-gmbh">
                <img src="https://www.vogella.com/img/common/linkedin_small.png" alt="Follow us on Linkedin" width="32" height="32" pagespeed_url_hash="110228678" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://www.xing.com/companies/vogellagmbh">
                <img src="https://www.vogella.com/img/common/xing_small.png" alt="Follow us on xing" width="32" height="32" pagespeed_url_hash="2568152418" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://github.com/vogellacompany">
                <img src="https://www.vogella.com/img/common/github_small.png" alt="vogella Github" width="32" height="32" pagespeed_url_hash="2344544139" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://vogella.com/blog/feed.xml">
				<img src="https://www.vogella.com/blog/assets/images/icons/rss_aqua.png" alt="RSS Feed" width="32" height="32" pagespeed_url_hash="2344544139" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
			</a>

        </div> <!-- social-icons -->

 	</div> <!-- footerrow -->

 	<div class="footerrow">
        <h2><a href="https://www.vogella.com/training/">Training</a></h2>

        <div class="footer_event">
            <div class="date_display">
                <a href="https://www.vogella.com/training/eclipse/eclipsercp.html">19 FEB</a>
            </div>
            <a href="https://www.vogella.com/training/eclipse/eclipsercp.html">Eclipse RCP dev.</a><br>
            <a href="https://www.vogella.com/training/eclipse/eclipsercp.html">(5 days)</a>
        </div>
         <div class="footer_event">
            <div class="date_display">
                <a href="https://learn.vogella.com">OT</a>
            </div><!-- date_display -->
            <a href="https://learn.vogella.com">Online training</a><br>
        </div>
    </div>
</div> <!-- companyfooter --></body>
</html>
---------------
Response from cache: Response{protocol=http/1.1, code=200, message=, url=https://www.vogella.com/}
Response URL: https://www.vogella.com/
Response Code: 200
Response Protocol: http/1.1
Response Message:
Response Handshake: Handshake{tlsVersion=TLS_1_3 cipherSuite=TLS_AES_256_GCM_SHA384 peerCertificates=[CN=vogella.com, CN=R3, O=Let's Encrypt, C=US, CN=ISRG Root X1, O=Internet Security Research Group, C=US] localCertificates=[]}
Response Cache Response: Response{protocol=http/1.1, code=200, message=, url=https://www.vogella.com/}
Response Network Response: null
Response Headers: last-modified: Mon, 03 Jun 2024 07:17:47 GMT
etag: "4fb6-619f72158796c-gzip"
accept-ranges: bytes
vary: Accept-Encoding
content-type: text/html
date: Mon, 03 Jun 2024 14:28:18 GMT
server: Apache/2.4.52 (Ubuntu)

Response Body:
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Eclipse, Android and Java training and support</title>

	<meta name="description" content="The vogella GmbH is a German company and provides services ranging from training, consulting and mentoring in the areas of Eclipse, Flutter and Git.">
	<meta name="keywords" content="Training, Tutorials, Exercises, Eclipse, RCP, OSGi, Android, GWT, JUnit, XML, JSF, JPA, Git">
	<meta name="verify-v1" content="sE7LNm8dZTyJgkDU7KR/1Hw5klYayq9ow10fOEcUHY0=">
	<meta name="robots" content="index, follow">
	<meta name="author" content="Lars Vogel">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<link href="https://www.vogella.com/css/navigation.css?v=2.9" rel="stylesheet" type="text/css">
	<link href="https://www.vogella.com/css/header.css?v=2.0" rel="stylesheet" type="text/css">
	<link href="https://www.vogella.com/css/companyfooter.css?v=2.0" rel="stylesheet" type="text/css">
	<link href="https://www.vogella.com/css/trainingoffering.css?v=2.2" rel="stylesheet" type="text/css">

	<link rel="shortcut icon" href="https://www.vogella.com/favicon.ico">
	<link rel="alternate" type="application/rss+xml" title="RSS" href="http://feeds.feedburner.com/EclipseAndJava">
	<link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="vogella" />
	<link rel="copyright" href="//creativecommons.org/licenses/by-nc-sa/3.0/de/">

	<!-- Google Analytics -->
	<script>
		window.dataLayer = window.dataLayer || [];
		function gtag(){dataLayer.push(arguments);}

		gtag('consent', 'default', {
			'ad_storage': 'denied',
			'analytics_storage': 'denied',
			'region': ['BE', 'BG', 'CZ', 'DK', 'DE', 'EE', 'IE', 'GR', 'ES', 'FR', 'HR', 'IT', 'CY', 'LV', 'LT', 'LU', 'HU', 'MT', 'NL', 'AT', 'PL', 'PT', 'RO', 'SI', 'SK', 'FI', 'SE', 'US-CA', 'US-CO'],
			'wait_for_update': 500
		});
		gtag('set', 'ads_data_redaction', true);
	</script>

	<script async src="https://www.googletagmanager.com/gtag/js?id=UA-3967758-1"></script>
	<script>
		window.dataLayer = window.dataLayer || [];
		function gtag(){dataLayer.push(arguments);}

		gtag('js', new Date());
		gtag('config', 'UA-3967758-1', {
			'anonymize_ip': true,
			'cookie_domain': 'vogella.com',
			'cookie_flags': 'SameSite=None;Secure'
		});
	</script>
	<!-- End of Google Analytics -->

 	<!-- Quantcast Choice. Consent Manager Tag v2.0 (for TCF 2.0) -->
	<script type="text/javascript" async=true>
	(function() {
	var host = window.location.hostname;
	var element = document.createElement('script');
	var firstScript = document.getElementsByTagName('script')[0];
	var url = 'https://cmp.quantcast.com'
    	.concat('/choice/', 'LU3TErYtTq8Gq', '/', host, '/choice.js?tag_version=V2');
	var uspTries = 0;
	var uspTriesLimit = 3;
	element.async = true;
	element.type = 'text/javascript';
	element.src = url;

	firstScript.parentNode.insertBefore(element, firstScript);

	function makeStub() {
		var TCF_LOCATOR_NAME = '__tcfapiLocator';
		var queue = [];
		var win = window;
		var cmpFrame;

	function addFrame() {
		var doc = win.document;
		var otherCMP = !!(win.frames[TCF_LOCATOR_NAME]);

		if (!otherCMP) {
			if (doc.body) {
				var iframe = doc.createElement('iframe');

				iframe.style.cssText = 'display:none';
				iframe.name = TCF_LOCATOR_NAME;
				doc.body.appendChild(iframe);
			} else {
				setTimeout(addFrame, 5);
			}
		}
		return !otherCMP;
	}

	function tcfAPIHandler() {
		var gdprApplies;
		var args = arguments;

		if (!args.length) {
			return queue;
		} else if (args[0] === 'setGdprApplies') {
			if (
			args.length > 3 &&
			args[2] === 2 &&
			typeof args[3] === 'boolean'
		) {
		gdprApplies = args[3];
		if (typeof args[2] === 'function') {
			args[2]('set', true);
		}
      }
      } else if (args[0] === 'ping') {
			var retr = {
			gdprApplies: gdprApplies,
			cmpLoaded: false,
			cmpStatus: 'stub'
		};

        if (typeof args[2] === 'function') {
          args[2](retr);
        }
      } else {
        if(args[0] === 'init' && typeof args[3] === 'object') {
          args[3] = Object.assign(args[3], { tag_version: 'V2' });
        }
        queue.push(args);
      }
    }

    function postMessageEventHandler(event) {
      var msgIsString = typeof event.data === 'string';
      var json = {};

      try {
        if (msgIsString) {
          json = JSON.parse(event.data);
        } else {
          json = event.data;
        }
      } catch (ignore) {}

      var payload = json.__tcfapiCall;

      if (payload) {
        window.__tcfapi(
          payload.command,
          payload.version,
          function(retValue, success) {
            var returnMsg = {
              __tcfapiReturn: {
                returnValue: retValue,
                success: success,
                callId: payload.callId
              }
            };
            if (msgIsString) {
              returnMsg = JSON.stringify(returnMsg);
            }
            if (event && event.source && event.source.postMessage) {
              event.source.postMessage(returnMsg, '*');
            }
          },
          payload.parameter
        );
      }
    }

    while (win) {
      try {
        if (win.frames[TCF_LOCATOR_NAME]) {
          cmpFrame = win;
          break;
        }
      } catch (ignore) {}

      if (win === window.top) {
        break;
      }
      win = win.parent;
    }
    if (!cmpFrame) {
      addFrame();
      win.__tcfapi = tcfAPIHandler;
      win.addEventListener('message', postMessageEventHandler, false);
    }
  };

  makeStub();

  var uspStubFunction = function() {
    var arg = arguments;
    if (typeof window.__uspapi !== uspStubFunction) {
      setTimeout(function() {
        if (typeof window.__uspapi !== 'undefined') {
          window.__uspapi.apply(window.__uspapi, arg);
        }
      }, 500);
    }
  };

  var checkIfUspIsReady = function() {
    uspTries++;
    if (window.__uspapi === uspStubFunction && uspTries < uspTriesLimit) {
      console.warn('USP is not accessible');
    } else {
      clearInterval(uspInterval);
    }
  };

  if (typeof window.__uspapi === 'undefined') {
    window.__uspapi = uspStubFunction;
    var uspInterval = setInterval(checkIfUspIsReady, 6000);
  }

// Google Analytics integration within Quantcast
	 window.__tcfapi('addEventListener', 2, function(tcData, success) {
       if (success && tcData.gdprApplies && (tcData.eventStatus === 'tcloaded' || tcData.eventStatus === 'useractioncomplete') ) {
         if (tcData.vendor.consents[755] && tcData.purpose.consents[1]) {
  		   gtag('consent', 'update', {
			 'ad_storage': 'granted',
			 'analytics_storage': 'granted'
		   });
		 }
	   }
	 })
	 })();
	</script>
	<!-- End Quantcast Choice. Consent Manager Tag v2.0 (for TCF 2.0) -->

	<!-- Open/close the sidenav -->
	<script>
		function openCloseNav() {
			if (document.getElementById("mobileTopnav").style.width === "150px")
			{
				document.getElementById("mobileTopnav").style.width = "0";
	        	document.body.style.marginLeft = "8px";
	        }
	        else
	        {
				document.getElementById("mobileTopnav").style.width = "150px";
	        	document.body.style.marginLeft = "160px";
	        }
		}
	</script>
	<!-- End of Open/close the sidenav -->

</head>

<body>

<div id="mobileHeaderpart">
    <span onclick="openCloseNav()"><img src="https://www.vogella.com/img/icons/burger.svg" alt="" /> </span>
    <div id="mobileLogo">
        <a title="vogella.com" href="http://www.vogella.com">
            <img src="https://www.vogella.com/img/logo/index_logo.svg" alt="vogella.com"/>
        </a>
    </div>
</div>

<nav id="mobileTopnav" class="sidenav">
    <a  class="currentpage" href="https://www.vogella.com/">Home</a>
	<a  href="https://www.vogella.com/tutorials/">Tutorials</a>
	<a  href="https://www.vogella.com/training/">Training</a>
    <a  href="https://www.vogella.com/consulting/">Consulting</a>
	<a  href="https://www.vogella.com/company/">Company</a>
	<a  style="margin-bottom:60px" href="https://www.vogella.com/contact.html">Contact us</a>
</nav>

<div id="headerpart">
	<div id="logo">
		<a title="vogella.com" href="http://www.vogella.com">
			<img src="https://www.vogella.com/img/logo/index_logo.png" alt="vogella.com"/>
		</a>
	</div>

	<nav id="topnav">
		<ul id="home" class="topnav">
			<li id="home-icon"  class="currentpage" ><a href="https://www.vogella.com/"></a></li>
			<li  > <a href="https://www.vogella.com/tutorials/">Tutorials</a>
				<ul>
					<li><a href="https://www.vogella.com/tutorials/eclipse.html">Eclipse RCP</a></li>
					<li><a href="https://www.vogella.com/tutorials/eclipseide.html">Eclipse IDE</a></li>
					<li><a href="https://www.vogella.com/tutorials/eclipseplatform.html">Eclipse IDE Extensions</a></li>
					<li><a href="https://www.vogella.com/tutorials/flutter.html">Flutter</a></li>
					<li><a href="https://www.vogella.com/tutorials/android.html">Android Programming</a></li>
					<li><a href="https://www.vogella.com/tutorials/web.html">Web</a></li>
					<li><a href="https://www.vogella.com/tutorials/java.html">Java</a></li>
					<li><a href="https://www.vogella.com/tutorials/technology.html">Technology</a></li>
					<li><a href="https://www.vogella.com/tutorials/algorithms.html">Software design</a></li>
					<li><a href="https://github.com/vogellacompany/">Code @ GitHub</a></li>
				</ul>
			</li>

			<li   ><a href="https://www.vogella.com/training/">Training</a>
				<ul>
					<li><a href="https://www.vogella.com/training/eclipse/eclipsercp.html">Eclipse RCP Training</a></li>
					<li><a href="https://www.vogella.com/training/testbuild/javatesting.html">Java Testing Training</a></li>
					<li><a href="https://www.vogella.com/training/appdevelopment/flutter.html">Cross Mobile App Dev. Training</a></li>
					<li><a href="https://www.vogella.com/training/git/index.html">Git Version Control Training</a></li>
					<li><a href="https://learn.vogella.com/">Self-study Portal</a></li>
					<li><a href="https://www.vogella.com/training/onsite/index.html">Other Onsite Training</a></li>
				</ul>
			</li>
			<li  ><a href="https://www.vogella.com/consulting/">Consulting</a></li>

			<li   ><a href="https://www.vogella.com/company/">Company</a>
				<ul>
					<li><a href="https://www.vogella.com/jobs/">Jobs</a></li>
					<li><a href="https://www.vogella.com/people/index.html">People</a></li>
					<li><a href="https://www.vogella.com/customers.html" onclick="_gaq.push(['_trackEvent', 'Customers', 'clicked', 'Header']);">Customers</a></li>
					<li><a href="https://vogella.com/blog/">Blog</a></li>
					<li><a href="https://www.vogella.com/books/">Books</a>
						<ul>
							<li><a href="https://www.vogella.com/books/eclipsercp.html">Eclipse RCP</a></li>
							<li><a href="https://www.vogella.com/books/eclipseide.html">Eclipse IDE</a></li>
							<li><a href="https://www.vogella.com/books/eclipsecontribution.html">Contributing to the Eclipse Project</a></li>
							<li><a href="https://www.vogella.com/books/git.html">Git</a></li>
						</ul>
					</li>
					<li><a href="https://www.vogella.com/research.html">Research Projects</a>
					<li><a href="https://www.paypal.com/donate?hosted_button_id=D2DMTGN3LJGQU">Donate</a></li>
				</ul>
			</li>

			<li   ><a href="https://www.vogella.com/contact.html">Contact us</a></li>
		</ul>
	</nav>

	<div id="searchfixed">
		<form action="https://www.vogella.com/search.html" id="cse-search-box">
			<div id="searchwrapper">
				<input type="hidden" name="cx" value="partner-pub-3851443674951530:3991491935" />
				<input type="hidden" name="cof" value="FORID:10" />
				<input type="hidden" name="ie" value="UTF-8" />
				<input type="text" placeholder="Search" id="search_field" name="q" size="55" />
				<input type="image" src="https://www.vogella.com/img/icons/srch.png" id="search_button" name="sa" alt="Search" />
			</div>
		</form>
	</div>
</div>

<div id="container_new">
<div id="leftcol">

<!-- Empty -->
</div> <!-- leftcolumn -->

<div class="content-wrapper">
	<div class="section_black">
		<div class="half_side">
			<a class="header_dark_background" href="https://www.vogella.com/company/"> vogella</a>
			<div class="description_white">
			We provide <a class="important_links" href="https://learn.vogella.com/">commercial online training</a>,
			<a class="important_links" href="https://www.vogella.com/training/onsite/index.html">onsite training</a>
			and
			<a class="important_links" href="https://www.vogella.com/consulting/">consulting</a>.
			We also publish lots of free
			<a class="important_links" href="https://www.vogella.com/tutorials/">tutorials</a>.
			</div> <!-- description_white -->
		</div> <!-- half_side -->

		<div class="half_side_center">
			<div class="small_img">
				<a href="https://www.vogella.com/company/">
					<img src="./img/logo/logo_very_rough_left_white.png" alt="vogella">
				</a>
			</div> <!--small_img-->
		</div> <!-- half_side -->
	</div> <!-- section_black -->

	<div class="section_gray">

		<div class="half_side_center">
			<div class="big_img">
				<a class="header_light_background" href="https://www.vogella.com/training/">
					<img src="./img/main_page/learningportaldevices.png" alt="LearningPortal">
				</a>
			</div>
		</div> <!-- half_side_center -->
		<div class="half_side">
			<a class="header_light_background" href="https://www.vogella.com/training/"> Training and education</a>
			<div class="description_black">
				Learn directly from our experts
				via our <a class="important_links" href="https://learn.vogella.com/">commercial online training</a>
				or <a class="important_links" href="https://www.vogella.com/training/onsite/index.html">directly from our employees</a>
				in the areas of <a class="important_links" href="https://learn.vogella.com/courses/details/rich-client-platform">Eclipse RCP</a>, Git, Java and mobile development.
			</div>
		</div> <!-- half_side -->

	</div> <!--section_gray-->

	<div class="section_white">
		<div class="half_side_center">
			<a href="https://www.vogella.com/tutorials/">
				<img src="./img/main_page/keywords.png" alt="Tutorials">
			</a>
		</div> <!-- half_side -->

		<div class="half_side">
			<a class="header_light_background" href="https://www.vogella.com/tutorials/"> Tutorials</a>
			<div class="description_black">Read our free online tutorials in the areas of Eclipse, RCP, Git, Java, Dart, Flutter and Web development and others.</div>
		</div> <!-- half_side -->
	</div> <!-- section_white -->


	<div class="section_gray_center">
		<div class="twothird_side_center">
			<a class="header_light_background" href="https://www.vogella.com/customers.html">Customer quotes</a>
			<div id="quotes"> </div>
		</div>
		<div class="third_side_center">
			<img class="customer-quote-img" src="./img/main_page/book_eyes.jpg" alt="Customers Quotes">
		</div>
	</div>

</div> <!-- content wrapper -->
<div id="rightcol">
<!--	<div id="banner2">
		<div id="banner-triangle2"> </div>
		<div id="banner-text2"> NOW <br> Hiring </div>
	</div>-->
</div> <!-- rightcol -->
</div>

<!-- Load jQuery and idTabs -->
<script type="text/javascript" src="https://www.vogella.com/javascript/jquery/jquery.js"></script> <!-- please change the src according to your folder strucutre and where jquery files will be on vogella.de -->
<script type="text/javascript" src="https://www.vogella.com/javascript/jquery/jquery_tabs.js"></script> <!-- please change the src according to your folder strucutre and where jquery files will be on vogella.de -->
<script type="text/javascript">var fade=function(id,s){s.tabs.removeClass(s.selected);s.tab(id).addClass(s.selected);s.items.hide();s.item(id).show();return false;};$.fn.fadeTabs=$.idTabs.extend(fade);$(".menu").fadeTabs();</script>
<!--
<script type="text/javascript">
$(document).ready(function() {
    // change the active tab after the site is loaded
   $('ul li a[href$="agenda"]').removeClass('selected');
   $('#agenda').hide();

   $('ul li a[href$="angebot"]').addClass('selected');
   $('#angebot').show();

});
</script>-->
<script type="text/javascript" src="./training/quotes/vogella/content-quotes.js"></script>
<script type="text/javascript" src="https://www.vogella.com/training/quotes/quotes.js"></script>
<div id="companyfooter">

	<div class="footerrow">
        <h2><a href="https://www.vogella.com/company/">vogella company</a></h2>
        <ul>
            <li><a href="https://www.vogella.com/customers.html" onclick="_gaq.push(['_trackEvent', 'Customers', 'clicked', 'Companyfooter']);">Customers</a></li>
            <li><a href="https://www.vogella.com/faq.html">FAQ</a></li>
            <li><a href="https://www.paypal.com/donate?hosted_button_id=D2DMTGN3LJGQU">Donate for free tutorials</a></li>
            <li><a href="https://www.vogella.com/legal.html" rel="nofollow">Legal</a></li>
            <li><a href="https://www.vogella.com/privacy.html" rel="nofollow">Privacy Policy</a></li>
            <li><a class="change-consent" onclick="window.__tcfapi('displayConsentUi', 2, function() {} );">Change consent</a></li>
        </ul>
    </div><!-- footerrow -->

    <div class="footerrow" id="contact_en">
        <h2><a href="https://www.vogella.com/contact.html?lang=en">Contact us</a></h2>

        <a href="https://vogella.com/contact_en.html" class="darker_link" >
        	<img alt="Email" src="https://www.vogella.com/img/icons/contact_form.svg" title="Email" style="width:20px"/>
        	Contact form
        </a>
        <br>
        <a href="mailto:sales@vogella.com" class="darker_link" >
        	<img alt="Email" src="https://www.vogella.com/img/icons/email_gray.svg" title="Email" style="width:20px"/>
        	sales@vogella.com
        </a>
        <br>
		<a href="tel:+49 40 7880 4360" class="darker_link" >
        <img alt="Call" src="https://www.vogella.com/img/icons/call_gray.svg" title="Call" style="width:20px"/>
        +49 40 7880 4360
		</a>

        <div id="social-icons">
            <a href="http://twitter.com/vogellacompany">
                <img src="https://www.vogella.com/img/common/twitter_small.png" alt="Follow us on twitter" width="32" height="32" pagespeed_url_hash="4214849013" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://www.linkedin.com/company/vogella-gmbh">
                <img src="https://www.vogella.com/img/common/linkedin_small.png" alt="Follow us on Linkedin" width="32" height="32" pagespeed_url_hash="110228678" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://www.xing.com/companies/vogellagmbh">
                <img src="https://www.vogella.com/img/common/xing_small.png" alt="Follow us on xing" width="32" height="32" pagespeed_url_hash="2568152418" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://github.com/vogellacompany">
                <img src="https://www.vogella.com/img/common/github_small.png" alt="vogella Github" width="32" height="32" pagespeed_url_hash="2344544139" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
            </a>
            <a href="https://vogella.com/blog/feed.xml">
				<img src="https://www.vogella.com/blog/assets/images/icons/rss_aqua.png" alt="RSS Feed" width="32" height="32" pagespeed_url_hash="2344544139" onload="pagespeed.CriticalImages.checkImageForCriticality(this);">
			</a>

        </div> <!-- social-icons -->

 	</div> <!-- footerrow -->

 	<div class="footerrow">
        <h2><a href="https://www.vogella.com/training/">Training</a></h2>

        <div class="footer_event">
            <div class="date_display">
                <a href="https://www.vogella.com/training/eclipse/eclipsercp.html">19 FEB</a>
            </div>
            <a href="https://www.vogella.com/training/eclipse/eclipsercp.html">Eclipse RCP dev.</a><br>
            <a href="https://www.vogella.com/training/eclipse/eclipsercp.html">(5 days)</a>
        </div>
         <div class="footer_event">
            <div class="date_display">
                <a href="https://learn.vogella.com">OT</a>
            </div><!-- date_display -->
            <a href="https://learn.vogella.com">Online training</a><br>
        </div>
    </div>
</div> <!-- companyfooter --></body>
</html>
---------------
Corrupted cache file: /Users/venkats/Documents/GitHub/CacheDir2/journal
Corrupted cache file: /Users/venkats/Documents/GitHub/CacheDir2/860badf5033dd8442c1f305ea3b8c956.0
Corrupted cache file: /Users/venkats/Documents/GitHub/CacheDir2/860badf5033dd8442c1f305ea3b8c956.1
Exception in thread "main" java.lang.NullPointerException
	at okhttp3.Cache$Entry.readCertificateList(Cache.kt:608)
	at okhttp3.Cache$Entry.<init>(Cache.kt:528)
	at okhttp3.Cache.get$okhttp(Cache.kt:177)
	at okhttp3.internal.cache.CacheInterceptor.intercept(CacheInterceptor.kt:47)
	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)
	at okhttp3.internal.http.BridgeInterceptor.intercept(BridgeInterceptor.kt:83)
	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)
	at okhttp3.internal.http.RetryAndFollowUpInterceptor.intercept(RetryAndFollowUpInterceptor.kt:76)
	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)
	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)
	at okhttp3.internal.connection.RealCall.execute(RealCall.kt:154)
	at com.venkatcodes.cache.OkHttpCacheExample.main(OkHttpCacheExample.java:81)
Disconnected from the target VM, address: '127.0.0.1:57128', transport: 'socket'

Process finished with exit code 1

    * */
}

