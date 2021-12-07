const fs = require('fs')

function costToAlign(goal, positions) {
    return positions.map(currPos => Math.abs(currPos - goal)).reduce((a, b) => a + b, 0)
}

function getSolutionPart1(positions) { // 329389
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlign(goal, positions)));
}

function costToAlignWithVariableRate(goal, positions) {
    return  positions.map(currPos => {
        let distance = Math.abs(currPos - goal);
        return distance * (1 +  distance) / 2;
    }).reduce((a, b) => a + b, 0);
}

function getSolutionPart2(positions) { // 86397080
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlignWithVariableRate(goal, positions)));
}

const part = process.env.part || "part1";
let positions = fs.readFileSync("input.txt").toString().trim().split(",").map(x => parseInt(x));
if (part === "part1")
    console.log(getSolutionPart1(positions))
else
    console.log(getSolutionPart2(positions))