import React from 'react';
import './App.css';
import {Container, Grid, Image, List} from 'semantic-ui-react'

function App() {
    return (
        <Grid divided relaxed>
            <Grid.Row>
                <Grid.Column width={10}>
                    <Grid>
                        <Grid.Column>
                            <Grid>
                                <Grid.Row stretched>
                                    <Grid.Column width={8}>
                                        <Container textAlign='left'>Left Aligned</Container>
                                    </Grid.Column>
                                    <Grid.Column width={8}>
                                        <Container textAlign='right'>Right Aligned</Container>
                                    </Grid.Column>
                                </Grid.Row>
                                <Grid.Row>
                                    <Grid stretched>
                                        <Grid.Column width={3}>
                                            <Image src={'img1.png'}></Image>
                                        </Grid.Column>
                                        <Grid.Column width={7}>
                                            <Container>Вытяжение внешней поверхности стопы</Container>
                                            <Container>На уровне щиколоток завязать ремешок (без крупных
                                                пряжек).
                                                Упражнение можно выполнять без ремешка, в этом случае нужно
                                                удерживать
                                                пятки рядом друг с другом силой мышц.
                                                Встать на колени (колени вместе, стопы вместе) и сесть ягодицами
                                                на
                                                пятки. Расправить руками пальцы стоп, все пальцы должны быть
                                                направлены
                                                назад. Тыльные стороны стопы прижать к полу. Мизинцы вытягивать
                                                назад,
                                                стараться сделать их длиннее больших пальцев.
                                                Находиться в позе 3-5 минут. Если появляется в стопах
                                                дискомфорт,
                                                выходить раньше.
                                                При выходе из позы сначала подвернуть пальцы на стопах, сесть на
                                                пятки и
                                                потом аккуратно вставать.</Container>
                                        </Grid.Column>
                                        <Grid.Column width={6}>
                                            <List.Content>
                                                <List>
                                                    <List.Item>
                                                        #Тэг 1
                                                    </List.Item>
                                                    <List.Item>
                                                        #Тэг 2
                                                    </List.Item>
                                                    <List.Item>
                                                        #Тэг 3
                                                    </List.Item>
                                                </List>
                                            </List.Content>
                                        </Grid.Column>
                                    </Grid>
                                </Grid.Row>
                            </Grid>
                        </Grid.Column>
                    </Grid>
                </Grid.Column>
                <Grid.Column width={6}>
                    <Container>Контролы</Container>
                </Grid.Column>
            </Grid.Row>
        </Grid>
    )
        ;
}

export default App;
