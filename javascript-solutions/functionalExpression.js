"use strict"

// :NOTE: use const when appropriate (fixed)
const variableNames = ["x", "y", "z"];
const cnst = (a) => () => a;
const variable = (a) => (...args) => args[variableNames.indexOf(a)];
const makeBinaryOperation = (operation) => (f, g) => (x, y, z) => operation(f(x, y, z), g(x, y, z));
const makeUnaryOperation = (operation) => (f) => (x, y, z) => operation(f(x, y, z));
const add = makeBinaryOperation((a, b) => a + b);
const subtract = makeBinaryOperation((a, b) => a - b);
const multiply = makeBinaryOperation((a, b) => a * b);
const divide = makeBinaryOperation((a, b) => a / b);
const negate = makeUnaryOperation((a) => -a);
const pi = () => Math.PI
const e = () => Math.E
const parse = (expression) => {
    let stack = [];
    const operations = {"*": multiply, "/": divide, "-": subtract, "+": add};
    // :NOTE: parsing of a constant number should go last (fixed)
    for (const token of expression.trim().split(" "))
        if (variableNames.indexOf(token) !== -1) {
            stack.unshift(variable(token))
        } else if (operations[token] !== undefined) {
            const expr1 = stack.shift();
            const expr2 = stack.shift();
            stack.unshift(operations[token](expr2, expr1));
        } else if (token === "negate") {
            const expr = stack.shift();
            stack.unshift(negate(expr))
        } else if (token === 'pi') {
            stack.unshift(pi)
        } else if (token === 'e') {
            stack.unshift(e)
        } else if (!isNaN(parseInt(token)))
            stack.unshift(cnst(parseInt(token)));
    return stack.shift();
}

let expression = add(
    subtract(
        multiply(
            variable("x"),
            variable("x")
        ),
        multiply(
            cnst(2),
            variable("x")
        )
    ),
    cnst(1)
)
for (let i = 0; i <= 10; i++) {
    console.log(expression(i, 0, 0));
}