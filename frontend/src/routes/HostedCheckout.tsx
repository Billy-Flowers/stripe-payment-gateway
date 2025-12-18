import {Center, Heading, VStack} from "@chakra-ui/react";
import {useState} from "react";
import CartItem, {type ItemData} from "../components/CartItem.tsx";
import TotalFooter from "../components/TotalFooter.tsx";
import CustomerDetails from "../components/CustomerDetails.tsx";
import {Products} from '../data.ts'

function HostedCheckout() {
    const [items] = useState<ItemData[]>(Products)
    return <>
        <Center minH={'100vh'} bg='white' color='black' py={4}>
            <VStack spacing='24px'>
                <Heading>Hosted Checkout Page</Heading>
                {items.map(elem => {
                    return <CartItem key={elem.id} data={elem} mode={'checkout'}/>
                })}
                <TotalFooter total={30} mode={"checkout"}/>
                <CustomerDetails data={items} endpoint={"/checkout/hosted"}/>
            </VStack>
        </Center>
    </>
}

export default HostedCheckout