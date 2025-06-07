export function formatDateDisplay(isoString: string): string {
    if (!isoString) return "-";
    const cleanString = isoString.split(".")[0];
    const date = new Date(cleanString);

    if (isNaN(date.getTime())) return "-";

    const pad = (n: number) => n.toString().padStart(2, "0");

    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1);
    const year = date.getFullYear();
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());

    return `${day}/${month}/${year} ${hours}:${minutes}`;
}