(define (makeResponse status body)
  (let ((response (construct (findClass "org.example.Response") status)))
    (dolistFn
     (lambda (elt) (call response "bodyAdd" (call elt "toString")))
     body)
    response))

(define (jellyNameEndpoint request)
  (makeResponse 200
                (list "ok so "
                      "I pull up "
                      "after party")))

;; define endpoints from jelly
(call router "bindJellyFunctionName"
      "/test/jelly/name" "jellyNameEndpoint")

(call router "bindJellyFunction"
      "/test/jelly/function" jellyNameEndpoint)

(call router "bindJellyFunction"
      "/test/jelly/lambda"
      (lambda (request)
        (makeResponse 200
                     (list "puttana la madonna comunque"))))
