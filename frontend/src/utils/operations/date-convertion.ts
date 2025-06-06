export function arrayToDate(dateArray: number[]): string {
    if (!dateArray || dateArray.length < 6) return "Invalid Date";
    const [year, month, day, hour, minute] = dateArray;
    return day + "/" + month + "/" + year + " - " + hour + ":" + minute;
}
