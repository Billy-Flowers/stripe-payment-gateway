import {type ItemData} from "./CartItem.tsx";
import {Button, Input, VStack} from "@chakra-ui/react";
import {useState} from "react";

function CustomerDetails(props: CustomerDetailsProp) {
    const [name, setName] = useState("")
    const [email, setEmail] = useState("")
    const onCustomerNameChange = (ev: React.ChangeEvent<HTMLInputElement>) => {
        setName(ev.target.value)
    }



    const onCustomerEmailChange = (ev: React.ChangeEvent<HTMLInputElement>) => {
        setEmail(ev.target.value)
    }

    const initiatePayment = () => {
        fetch(import.meta.env.VITE_SERVER_BASE_URL + props.endpoint, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                items: props.data.map(elem => ({
            id: elem.id,
            name: elem.name,
            object: "product",
            active: true,
            created: Math.floor(Date.now() / 1000),
            default_price: null,
            description: elem.description,
            images: [elem.image],
            livemode: false,
            metadata: {},
            package_dimensions: null,
            shippable: null,
            statement_descriptor: null,
            tax_code: null,
            type: "good",
            unit_label: null,
            updated: Math.floor(Date.now() / 1000),
            url: null
            })),
                customerName: name,
                customerEmail: email,
            })
        })
            .then(r => r.text())
            .then(r => {
                window.location.href = r
            })

    }

    return <>
        <VStack spacing={3} width={'xl'}>
            <Input variant='filled' placeholder='Customer Name' onChange={onCustomerNameChange} value={name}/>
            <Input variant='filled' placeholder='Customer Email' onChange={onCustomerEmailChange} value={email}/>
            <Button onClick={initiatePayment} colorScheme={'green'}>Checkout</Button>
        </VStack>
    </>
}

interface CustomerDetailsProp {
    data: ItemData[]
    endpoint: string
}

export default CustomerDetails