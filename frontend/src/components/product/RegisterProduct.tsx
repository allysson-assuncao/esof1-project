import React from "react";
import {AddProductForm} from "@/components/form/add/AddProductForm";

export function RegisterProduct (){

    return (<div className="container w-full mx-auto p-4">
        <h1 className="text-3xl font-bold mb-6 text-center">Novo produto</h1>
        <div>
            <AddProductForm/>
        </div>
    </div>)
}