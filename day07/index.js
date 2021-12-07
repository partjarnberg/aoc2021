const fs = require('fs')

function costToAlign(goal, positions, costFunction) {
    return positions.map(currPos => costFunction(currPos)).reduce((a, b) => a + b, 0)
}

function getSolutionPart1(positions) { // 329389
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlign(goal, positions, function (currPos) {
        return Math.abs(currPos - goal);
    })));
}

function getSolutionPart2(positions) { // 86397080
    return Math.min(...[...Array(Math.max(...positions) + 1).keys()].map(goal => costToAlign(goal, positions, function (currPos) {
        let distance = Math.abs(currPos - goal);
        return distance * (1 +  distance) / 2;
    })));
}

const part = process.env.part || "part2";
let positions = fs.readFileSync("input.txt").toString().trim().split(",").map(x => parseInt(x));
if (part === "part1")
    console.log(getSolutionPart1(positions))
else
    console.log(getSolutionPart2(positions))