"use strict"

const variableNames = ["x", "y", "z"]
const ZeroConst = new Const(0)
const OneConst = new Const(1)

// :NOTE: inefficient zero/one constants(fixed)
function Const(value) {
    this.value = value
}

Const.prototype.evaluate = function () {
    return this.value
}
Const.prototype.toString = function () {
    return this.value.toString()
}
Const.prototype.diff = () => ZeroConst

Const.prototype.prefix = Const.prototype.toString

// :NOTE: non-canonical, obscured use of prototypes(fixed)
function Variable(variableName) {
    this.variableName = variableName
}

Variable.prototype.evaluate = function (...args) {
    return args[variableNames.indexOf(this.variableName)]
}
Variable.prototype.toString = function () {
    return this.variableName;
}
Variable.prototype.diff = function (variable) {
    return variable === this.variableName ? OneConst : ZeroConst;
}

Variable.prototype.prefix = Variable.prototype.toString

function OperationPrototype(operation, operationSign, diffRule) {
    this.operation = operation
    this.operationSign = operationSign
    this.diffRule = diffRule
    this.functions = []
}

OperationPrototype.prototype.diff = function (variable) {
    return this.diffRule(...this.functions)(variable)
}
OperationPrototype.prototype.evaluate = function (x, y, z) {
    return this.operation(...this.functions.map((a) => a.evaluate(x, y, z)))
}

OperationPrototype.prototype.toString = function () {
    return this.functions.map((a) => a.toString()).join(" ") + ` ${this.operationSign}`
}

OperationPrototype.prototype.prefix = function () {
    return `(${this.operationSign} ${this.functions.map((a) => a.prefix()).join(" ")})`
}

function makeOperation(operation, operationSign, diffRule) {
    const resultFunction = function CreateFunction(...functions) {
        this.functions = [...functions]
    }
    resultFunction.prototype = Object.create(new OperationPrototype(operation, operationSign, diffRule))

    return resultFunction
}


const Add = makeOperation((a, b) => a + b, "+", (f, g) => (variable) => new Add(f.diff(variable), g.diff(variable)))
const Subtract = makeOperation((a, b) => a - b, "-", (f, g) => (variable) => new Subtract(f.diff(variable), g.diff(variable)))
const Multiply = makeOperation((a, b) => (a * b), "*", (f, g) => (variable) => new Add(new Multiply(f.diff(variable), g), new Multiply(f, g.diff(variable))))
const Divide = makeOperation((a, b) => (a / b), "/", (f, g) => (variable) => new Divide(new Subtract(new Multiply(f.diff(variable), g), new Multiply(f, g.diff(variable))), new Multiply(g, g)))
const Negate = makeOperation((a) => (-a), "negate", (f) => (variable) => new Negate(f.diff(variable)))
const Sinh = makeOperation((a) => Math.sinh(a), "sinh", (f) => (variable) => new Multiply(f.diff(variable), new Cosh(f)))
const Cosh = makeOperation((a) => Math.cosh(a), "cosh", (f) => (variable) => new Multiply(f.diff(variable), new Sinh(f)))


const binaryOperations = {"*": Multiply, "/": Divide, "-": Subtract, "+": Add};
const unaryOperations = {"negate": Negate, "sinh": Sinh, "cosh": Cosh}

const parse = (expression) => {
    const stack = [];
    for (const token of expression.trim().split(" "))
        if (variableNames.indexOf(token) !== -1) {
            stack.unshift(new Variable(token));
        } else if (binaryOperations[token] !== undefined) {
            const expr1 = stack.shift();
            const expr2 = stack.shift();
            stack.unshift(new binaryOperations[token](expr2, expr1));
        } else if (unaryOperations[token] !== undefined) {
            const expr = stack.shift();
            stack.unshift(new unaryOperations[token](expr))
            // :NOTE:/2 unify handling of binary and unary?
        } else if (!isNaN(parseInt(token)))
            stack.unshift(new Const(parseInt(token)));
    return stack.shift();
}

class BaseParser {
    constructor(expression) {
        this.expression = expression.trim()
        this.pos = 0
        this.END = "END_OF_STRING"
        this.ch = undefined
        this.take()
    }

    hasNext() {
        return this.pos < this.expression.length;
    }

    next() {
        return this.expression[this.pos++]
    }

    take() {
        let result = this.ch
        this.ch = this.hasNext() ? this.next() : this.END
        return result
    }

    takeIfTest(expected) {
        if (this.test(expected)) {
            this.take()
            return true;
        }
        return false;
    }

    test(expected) {
        return this.ch === expected
    }

    expect(token) {
        if (!this.test(token)) {
            throw new StringParserError(`expected ${token}, found ${this.ch}`)
        } else
            this.take()
    }

    skipWhitespaces() {
        while (this.takeIfTest(' ')) {

        }
    }

    peekNext() {
        return this.expression[this.pos]
    }

}

class ExpressionPrefixParser extends BaseParser {
    constructor(expression) {
        super(expression);
    }

    startParse() {
        let result = this.parseFirstValue()
        if (this.ch !== this.END)
            throw new PrefixParserError(`expected END of the expression, found ${this.ch}`)
        return result
    }

    parsePrefix() {
        this.skipWhitespaces()
        let firstBracket = false
        if (this.takeIfTest('('))
            firstBracket = true
        let result = this.parseOperation()
        if (firstBracket) {
            this.skipWhitespaces()
            this.expect(")")
        }
        return result
    }

    parseFirstValue() {
        let result
        try {
            result = this.parseConst()
        } catch (e) {
            try {
                result = this.parseVariable()
            } catch (e) {
                result = this.parsePrefix()
            }
        }
        return result
    }

    parseValue() {
        this.skipWhitespaces()
        const token = this.ch
        if (variableNames.indexOf(token) !== -1) {
            return this.parseVariable()
        } else if (token === '-' || !isNaN(parseInt(token)))
            return this.parseConst();
        else if (token === '(') {
            return this.parsePrefix()
        } else
            throw new PrefixParserError(`argument expected, found ${this.ch}`)
    }

    parseOperation() {
        this.skipWhitespaces()
        let token = ""
        while (this.hasNext() && !this.test(' ') && !this.test('(')) {
            token += this.take()
        }
        if (binaryOperations[token] !== undefined) {
            return new binaryOperations[token](this.parseValue(), this.parseValue())
        } else if (unaryOperations[token] !== undefined) {
            return new unaryOperations[token](this.parseValue())
        }
        throw new ParseOperationError(`operation expected, found ${token}`)
    }

    parseConst() {
        this.skipWhitespaces()
        let isNegative = false
        if (this.ch === '-') {
            isNegative = true
            if (!this.hasNext() || ('0' > this.peekNext() || this.peekNext() > '9'))
                throw new ConstError(`negative const expected, found ${this.ch}`);
            this.take();
        }
        let result = ""
        while ('0' <= this.ch && this.ch <= '9') {
            result += this.take()
        }
        if (result === "")
            throw new ConstError(`const expected, found ${this.ch}`)
        let parsedInt = parseInt(result)
        if (isNegative)
            parsedInt *= -1;
        return new Const(parsedInt)
    }

    parseVariable() {
        if (variableNames.indexOf(this.ch) !== -1) {
            return new Variable(this.take())
        }
        throw new VariableNameError(`invalid variable name, found ${this.ch}`)
    }
}

const parsePrefix = (expression) => {
    const expressionParser = new ExpressionPrefixParser(expression)
    return expressionParser.startParse()
}

function StringParserError(message) {
    Error.call(this, message)
    this.message = message
}

StringParserError.prototype = Object.create(Error.prototype)


function PrefixParserError(message) {
    Error.call(this, message)
    this.message = message
}

PrefixParserError.prototype = Object.create(Error.prototype)
PrefixParserError.prototype.constructor = PrefixParserError

function VariableNameError(message) {
    PrefixParserError.call(this, message)
}

VariableNameError.prototype = Object.create(PrefixParserError.prototype)
VariableNameError.prototype.constructor = VariableNameError


function ConstError(message) {
    PrefixParserError.call(this, message)
}

ConstError.prototype = Object.create(PrefixParserError.prototype)
ConstError.prototype.constructor = ConstError

function ParseOperationError(message) {
    Error.call(this, message)
    this.message = message
}

ParseOperationError.prototype = Object.create(Error.prototype)
ParseOperationError.prototype.constructor = ParseOperationError

let expr1 = new Subtract(
    new Multiply(
        new Const(2),
        new Variable("x")
    ),
    new Const(3)
);

let res1 = parsePrefix("           (/(negate x)2)    ")
console.log(expr1.toString())
console.log(expr1.evaluate(0, 0, 0))
console.log(expr1.diff("x").toString());
