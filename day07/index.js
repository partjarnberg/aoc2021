const fs = require('fs')

function horizontalPositions(filename="input.txt") {
    return fs.readFileSync(filename).toString().trim().split(",").map((x)=>parseInt(x))
}

function costToAlign(goal, positions) {
    return positions.map(currentPosition => Math.abs(currentPosition - goal)).reduce((a, b) => a + b, 0)
}

function getSolutionPart1() { // 329389
    const positions = horizontalPositions();
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlign(goal, positions)));
}

function costToAlignWithVariableRate(goal, positions) {
    return  positions.map(position => {
        let num = 0;
        for (let i = Math.abs(position - goal); i > 0; i--) {
            num = num + i;
        }
        return num;
    }).reduce((a, b) => a + b, 0);
}

function getSolutionPart2() { // 86397080
    const positions = horizontalPositions();
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlignWithVariableRate(goal, positions)));
}

const part = process.env.part || "part1"

if (part === "part1")
    console.log(getSolutionPart1())
else
    console.log(getSolutionPart2())

module.exports = {
    getSolutionPart1, getSolutionPart2, inputDataLinesIntegers: horizontalPositions
}