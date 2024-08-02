;; utilities unrelated to requests &Co.
(loadFile "utils.scm")

;; utilities related to request &Co.
(define-library (request parsing)
  (import (only (list utils) fromJava))
  (export requestMethod
          requestRemainingPath
          requestHeaders)
  (begin
    (define (requestMethod request) (call request "getMethod"))
    (define (requestRemainingPath request) (fromJava (call request "getRemainingUrlPath")))
    (define (requestHeaders request) (call request "getHeaders"))))

(define-library (request handling)
  (import (request parsing))
  (import (string utils))
  (export methodDispatcher)
  (begin
    ;; desired interface
    #|
    (define handler
      (methodDispatch
       "GET" (lambda (request) (do-thing-with request))
       "POST" (lambda (request) (do-thing-with request))
       #t (lambda (request) (makeResponse 404 "fuck that"))))

    (I didn't implement `cond' :,)
    |#
    (define (methodDispatcher &rest lst)
      (lambda (request)
        (do ((methActs lst (cdr (cdr methActs))))
            ((or (null? methActs)
                 (equal? (car methActs) #t)
                 (ciequals? (car methActs)
                            (requestMethod request)))
             (unless (null? methActs)
               ((nth methActs 1) request))))))
    ))

;; server config
(define-library (jelly server config)
  (export makeResponse
          bindEndpointFunName bindEndpointFun)
  (begin
    (define (makeResponse status body)
      (let ((response (construct (findClass "org.example.Response") status)))
        (dolistFn
         (lambda (elt) (call response "bodyAdd" (call elt "toString")))
         body)
        response))

    (define (bindEndpointFunName ep funName)
      (call router "bindJellyFunctionName" ep funName))

    (define (bindEndpointFun ep fn)
      (call router "bindJellyFunction" ep fn))))

(define-library (jelly server start)
  (import (only (list utils) intersperse)
          (only (parse utils) stoi)
          (request handling)
          (request parsing)
          (jelly server config))
  (export bindAll)
  (begin
    (define (mkResp code &rest body)
      (makeResponse code (intersperse "\n" body)))

    (define (bindAll)
      (println "dispatch")
      (bindEndpointFun
       "/test/jelly/dispatch"
       (methodDispatcher
        "get" (lambda (request) (mkResp 200 "getty getty getty"))
        "post" (lambda (request) (mkResp 200 "posty posty posty"))
        "delete" (lambda (request) (mkResp 200 "cacca addosso"))))

      (println "lambda")
      (bindEndpointFun
       "/test/jelly/lambda"
       (lambda (request)
         (mkResp
          200
          "Now, this is a story all about how"
          "My life got flipped-turned upside down"
          "And I'd like to take a minute"
          "Just sit right there"
          "I'll tell you how I became the prince of a town called Bel-Air")))

      (println "add")
      (bindEndpointFun
       "/test/jelly/add"
       (lambda (request)
         (mkResp 200 (call
                      (reduce + (map stoi (requestRemainingPath request))
                              0)
                      "toString")))))))

(import (only (jelly server start) bindAll))
(bindAll)

(println "LOOOOOOAD")
