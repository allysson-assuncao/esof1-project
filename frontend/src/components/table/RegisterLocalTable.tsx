import React from "react";
import {AddLocalTableForm} from "@/components/form/add/AddLocalTableForm";

export function RegisterLocalTable (){

    return (<div className="container w-full mx-auto p-4">
        <h1 className="text-3xl font-bold mb-6 text-center">Nova Mesa</h1>
        <div>
            <AddLocalTableForm/>
        </div>
    </div>)
}