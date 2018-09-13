
var CACHE_NAME = 'Clap-Trap-Bot-V1';
var urlsToCache = [
    '/',
    '/music',
    '/register',
    '/oauthCallback',
    '/css/materialize.css',
    '/js/navabar.js',
    '/manifest.json'

];




self.addEventListener('install', function(event) {
    // Perform install steps
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(function(cache) {
                console.log('Opened cache');
                return cache.addAll(urlsToCache);
            })
    );
});

self.addEventListener('fetch', function(event) {
    event.respondWith(
        caches.match(event.request)
            .then(function(response) {
                    // Cache hit - return response
                    if (response) {
                        return response;
                    }
                    return fetch(event.request);
                }
            )
    );
});