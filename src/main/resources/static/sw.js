var CACHE_NAME = 'Clap-Trap-Bot-V0.1';
var urlsToCache = [
    '/',
    '/music',
    '/register',
    '/oauthCallback',
    '/css/materialize.css',
    '/js/navabar.js',
    '/js/materialize.js',
    '/js/jquery-3.3.1.min.js',
    '/js/js.cookie.js',
    '/manifest.json'

];


self.addEventListener('install', function (event) {
    // Perform install steps
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(function (cache) {
                console.log('Opened cache');
                return cache.addAll(urlsToCache);
            })
    );
});

self.addEventListener('fetch', function (event) {
    event.respondWith(
        caches.match(event.request)
            .then(function (response) {
                    // Cache hit - return response
                    if (response) {
                        return response;
                    }
                    return fetch(event.request);
                }
            )
    );

    event.waitUntil(
        update(event.request)
            .then(refresh)
    );

    function update(request) {
        return caches.match(request).then(
            function (response) {
                if (response) {
                    return caches.open(CACHE_NAME).then(function (cache) {
                        return fetch(request).then(function (response) {
                            return cache.put(request, response.clone()).then(function () {
                                return response;
                            });
                        });
                    });
                }
            }
        );
    }

    function refresh(response) {
        if(response){
            return self.clients.matchAll().then(function (clients) {
                clients.forEach(function (client) {
                    var message = {
                        type: 'refresh',
                        url: response.url,
                        eTag: response.headers.get('eTag')
                    };
                    client.postMessage(JSON.stringify(message));
                });
            });
        }

    }


});