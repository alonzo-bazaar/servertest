(define-library (parse utils)
  (export stoi)
  (begin
    (define (stoi s)
      (callStatic (findClass "java.lang.Integer") "parseInt" s))))

(define-library (string utils)
  (export upcase downcase  ciequals?)
  (begin
    (define (upcase s) (call s "toUpperCase"))
    (define (downcase s) (call s "toUpperCase"))
    (define (ciequals? s1 s2) (call s1 "equalsIgnoreCase" s2))))

(define-library (list utils)
  (export flatten intersperse toJava fromJava)
  (begin
    (define (flatten lst)
      (reduce append lst nil))

    (define (intersperse separator lst)
      (cdr (flatten
            (map (lambda (elt) (list separator elt))
                 lst))))

    (define (toJava lst)
      (callStatic (findClass "org.jelly.utils.ConsUtils") "toList" lst))

    (define (fromJava lst)
      (callStatic (findClass "org.jelly.utils.ConsUtils") "toCons" lst))
    ))

(define-library (join strings)
  (import (only (list utils) intersperse))
  (export joinStrings joinStringsSeparator)
  (begin
    (define (joinStringsList strings)
      (let ((sb (construct (findClass "java.lang.StringBuilder"))))
        (dolistFn
         (lambda (x) (call sb "append" x))
         strings)
        (call sb "toString")))

    (define (joinStrings &rest strings)
      (joinStringsList strings))

    (define (joinStringsSeparator separator &rest strings)
      (let ((acc (list)))
        (joinStringsList (intersperse separator strings))))))
