prime(N) :- N1 is N - 1, isPrime(N, N1).

isPrime(N, 1) :- !.
isPrime(N, 0) :- !.
isPrime(N, R) :- \+ 0 is mod(N, R), R1 is R - 1, isPrime(N, R1).

composite(N) :- \+ prime(N).

prime_divisors(1, []) :- !.
prime_divisors(N, Divisors) :- find_divisors(1, 2, N, Divisors).

unique_prime_divisors(N, Divisors) :- find_divisors(0, 2, N, Divisors).

concat([], B, B).
concat([H | T], B, [H | R]) :- concat(T, B, R).

range(L, L, V, [V]).
range(N, L, V, [V | T]) :- N < L, N1 is N + 1, range(N1, L, V, T).

power(1, B, 1) :- !.
power(A, 1, A) :- !.
power(A, B, R) :-
	R > 1,
	0 is mod(R, A),
	R1 is div(R, A), power(A, B1, R1),
	B is B1 + 1, !.
power(A, 0, R).

get_array_of_number(R, D, ResList) :- power(D, S, R), range(1, S, D, ResList), !.
get_array_of_number(R, D, ResList) :- range(1, 1, D, ResList).

find_divisors(F, N, L, []) :- N > L.
find_divisors(F, N, L, List) :-
    1 is F,
    N =< L,
    0 is mod(L, N),
    prime(N),
    R is div(L, N), !,
    N1 is N + 1,
    get_array_of_number(L, N, ResList), find_divisors(F, N1, L, T), concat(ResList, T, List).
find_divisors(F, N, L, List) :-
    0 is F,
    N =< L,
    0 is mod(L, N),
    prime(N),
    R is div(L, N), !,
    N1 is N + 1,
    find_divisors(F, N1, L, T), concat([N], T, List).
find_divisors(F, N, L, R) :-
	N =< L,
	N1 is N + 1,
	find_divisors(F, N1, L, R).