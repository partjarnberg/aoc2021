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
        let distance = Math.abs(position - goal);
        return distance * (1 +  distance) / 2;
    }).reduce((a, b) => a + b, 0);
}

function getSolutionPart2() { // 86397080
    const positions = horizontalPositions();
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlignWithVariableRate(goal, positions)));
}

const part = process.env.part || "part2"

if (part === "part1")
    console.log(getSolutionPart1())
else
    console.log(getSolutionPart2())

module.exports = {
    getSolutionPart1, getSolutionPart2, inputDataLinesIntegers: horizontalPositions
}