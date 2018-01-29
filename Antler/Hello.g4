grammar Hello;
@header{
package src;
}
ap
	: 'doc("' Letter '")' '/' rp #apSingleSlash
	| 'doc("' Letter '")' '//' rp #apDoubleSlash
	;

rp
    : Letter #rpTagName
    | '*'   #rpAllChildren
    | '.'   #rpCurrent
    | '..'  #rpParent
    | 'text()' #rpGetTextNode
    | '@' Letter #rpGetAttribute
    | '@' '*'  #rpAllAttribute
    | '(' rp ')' #rpParenthesis
    | rp '/' rp #rpSingleSlash
    | rp '//' rp #rpDoubleSlash
    | rp '[' f ']' #rpFilter
    | rp ',' rp #rpQuote
    ;

f
	: rp #filterRp
	| NUM  #filterindex
	| rp '=' '"' Letter '"' #filterAttribute
	| rp ('='|'eq') rp #filterEq
	| rp ('=='|'is') rp #filterIs
	| '(' f ')' #filterQuote
	| f 'and' f #filterAnd
	| f 'or' f #filterOr
	| 'not' f #filterNot
	;

NUM : [0-9]+ ;

Letter : [a-zA-Z0-9_.-]+ ;

WS  : [ \t\r\n]+ -> skip ;    // toss out whitespace