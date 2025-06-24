import {AddWorkstationForm} from "@/components/form/add/AddWorkstationForm";
import React from "react";


export function WorkstationRegister (){


    return (<div className="container w-full mx-auto p-4">
        <h1 className="text-3xl font-bold mb-6 text-center">Nova destinação</h1>
        <div>
            <AddWorkstationForm />
        </div>
    </div>)
}