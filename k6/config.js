const getEnv = (key, defaultValue) => {
    const value = __ENV[key];
    if(!value&&!defaultValue){
        throw new Error(`Env not found for ${key}`)
    }
    return value?value:defaultValue;
}

export const API_AUTH_TOKEN = getEnv("API_AUTH_TOKEN");
export const API_ROOT_URL = getEnv("API_ROOT_URL","https://localhost");
export const NR_AUTH_TOKEN = getEnv("NR_AUTH_TOKEN","ccbab550e40b0d987fa9133f959c7c710bf0NRAL");