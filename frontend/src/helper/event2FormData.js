export function event2FormData(e){
    const form = e.target;

    const formDataObj = new FormData(form);
    const data = {};
    for (let [key, value] of formDataObj.entries()) {
        data[key] = value;
    }
    return formDataObj

}